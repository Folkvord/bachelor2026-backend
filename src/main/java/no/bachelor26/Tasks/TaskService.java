package no.bachelor26.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.DTO.TaskComponents;
import no.bachelor26.Tasks.DTO.RawTaskComponents;
import no.bachelor26.Tasks.DTO.TaskSeed;
import no.bachelor26.Tasks.Exception.TaskNotFoundException;
import no.bachelor26.Tasks.Hints.HintService;
import no.bachelor26.Tasks.Hints.DTO.HintResult;
import no.bachelor26.Tasks.TaskSessions.TaskSessionService;
import no.bachelor26.User.UserSessions.UserSession;
import no.bachelor26.User.UserSessions.UserState;
import no.bachelor26.WebSocket.WebSocketSender;
import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;


/**
 * Service-klassen som har ansvaret for oppgavene og oppgaveAPI-ene.
 * 
 * @author Kristoffer Folkvord
 */
@Service
public class TaskService {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired TaskRepository taskRepo;
    @Autowired ObjectMapper objectMapper;
    @Autowired WebSocketSender sender;
    @Autowired TaskProcesser taskProcesser;
    @Autowired HintService hintService;
    @Autowired TaskSessionService taskSessionService;

    static final String TASK_FILE_PATH = "/static/tasks/";



    /**
     * Henter og prosesserer innholdet til en oppgave.
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Det prosesserte innholdet til en oppgave og flagget
     */
    public TaskComponents getAndProcessTaskComponents(Long id) throws TaskNotFoundException{
        RawTaskComponents rawTaskComponents = taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );

        // Finn ut flagget her
        String flag = rawTaskComponents.getStaticFlag() == null ? 
            "BunOS{BUNNI}" : rawTaskComponents.getStaticFlag();

        TaskComponents result = new TaskComponents();
        result.setFlag(flag);
        result.setData(
            rawTaskComponents.getData()
        );
        result.setHints(
            hintService.getTaskHints(id)
        );

        return taskProcesser.process(result);
    }



    /**
     * API-et for henting av oppgaveinformasjon.
     * SKAL RETURNERE ID-ENE TIL OPPGAVENE BRUKEREN HAR TILGANG TIL
     * 
     * @param userID ID-en på klienten som kaller API-et
     */
    public void respondToTaskInfo(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage("task-info");
        reply.setRequestID(msg.getRequestID());
        UUID userID = userSession.getUserID();

        // Blabla hent tilgjengelige oppgaver (MIDLERTIDIG HARDKODET)
        List<Long> tempAvaliableTasks = new ArrayList<>();
        tempAvaliableTasks.add(Long.valueOf(0));
        tempAvaliableTasks.add(Long.valueOf(1));
        tempAvaliableTasks.add(Long.valueOf(2));

        reply.setData(
            objectMapper.valueToTree(
                tempAvaliableTasks
            )
        );

        sender.send(userID, reply);

    }



    /**
     * API-et for å hente innholdet til en oppgave.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param data Innholdet til klientmeldingen
     */
    public void respondToTask(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage("task");
        reply.setRequestID(msg.getRequestID());
        UUID userID = userSession.getUserID();

        // Er innholdet en long?
        JsonNode content = msg.getData().get("taskID");
        if(content == null || !content.canConvertToLong()){
            sender.sendError(userID, reply, "invalid content");
            return;
        }

        Long taskID = content.asLong();

        
        // Har brukeren tilgang til oppgaven?


        // Hent prosessert oppgave
        TaskComponents TaskComponents;
        try{
            TaskComponents = getAndProcessTaskComponents(taskID);
        } catch(TaskNotFoundException e){
            sender.sendError(userID, reply, "no access");
            return;
        }

        // WTF SKAL SKJE DERSOM DET ER EN DUPE SESJON
        boolean startedSession = taskSessionService.startTaskSession(
            userID, taskID, TaskComponents.getFlag(), TaskComponents.getHints()
        );

        if(!startedSession){
            sender.sendError(userID, reply, "dupe session");
            return;
        }

        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                TaskComponents.getData()
            )
        );
        sender.send(userID, reply);

        userSession.setState(UserState.PARSE_STANDBY);
    }



    /**
     * API-et som responderer til parsestatusmeldingen sendt etter klienten parser oppgaven.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToParseStatus(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage("parse-status");
        reply.setRequestID(msg.getRequestID());
        UUID userID = userSession.getUserID();

        boolean userInSession = taskSessionService.userInActiveSession(userID);
        if(!userInSession){
            userSession.setState(UserState.IDLE);
            sender.sendError(userID, reply, "no session");
            return;
        }

        reply.setStatus("success");
        switch(msg.getStatus()){
            case "success":
                userSession.setState(UserState.ACTIVE_TASK);
                break;

            case "error":
                taskSessionService.cancelTaskSession(userID);
                userSession.setState(UserState.IDLE);
                break;

            default:
                userSession.setState(UserState.IDLE);
                sender.sendError(userID, reply, "wtf");
                return;
        }

        sender.send(userID, msg);
    }



    /**
     * API-et for å avbryte en oppgavesesjon.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToCancelTask(UserSession userSession, GameMessage msg){
        UUID userID = userSession.getUserID();
        
        if(msg.getStatus().equals("error")){
            String reason = msg.getData().get("desc").asString();   // Tilgi meg Ian
            log.error("UserID (" + userID + "): Avbrøt oppgavesesjonen sin på grunn av en feil: " + reason + ".");
        }

        taskSessionService.cancelTaskSession(userID);
        userSession.setState(UserState.IDLE);
    }



    /**
     * API-et for å validere et flagg.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToValidateFlag(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage("validate-flag");
        reply.setRequestID(msg.getRequestID());
        UUID userID = userSession.getUserID();

        if(!taskSessionService.userInActiveSession(userID)){
            sender.sendError(userID, reply, "no session");
            return;
        }

        if(msg.getData() == null || !msg.getData().has("flag")){
            sender.sendError(userID, reply, "invalid data format");
            return;
        }

        String flag = "wiener";
        try{
            flag = msg.getData().get("flag").asString();
        } catch(JsonNodeException e){
            sender.sendError(userID, reply, "invalid flag format");
            return;
        }

        String result = taskSessionService.validateFlag(userID, flag) ? "correct" : "wrong";
        if(result.equals("correct")){
            taskSessionService.cancelTaskSession(userID);
            userSession.setState(UserState.IDLE);
        }

        reply.setStatus("success");
        reply.setData(
            objectMapper.readTree("{\"result\":\"" + result + "\"}")
        );

        sender.send(userID, reply);
    }

    

    /**
     * Henter et hint til en bruker i en oppgavesesjon
     * 
     * @param userSession Spillerens tilstand
     * @param msg GameMessage
     */
    public void respondToGetHint(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage("get-hint");
        reply.setRequestID(msg.getRequestID());
        UUID userID = userSession.getUserID();

        if(msg.getData() == null){
            sender.sendError(userID, reply, "no data");
            log.error("UserID (" + userID + "): Hintmelding uten data");
            return;
        }

        if(!msg.getData().has("index")){
            sender.sendError(userID, reply, "invalid data format");
            log.error("UserID (" + userID + "): Hintmelding med dårlig innhold");
            return;
        }

        int index = msg.getData().get("index").asInt();
        HintResult hintResult = taskSessionService.retrieveHint(userID, index);

        if(hintResult.getStatus() != HintResult.Status.OK){
            sender.sendError(userID, reply, "invalid-hint");

            // Hvis dette er sant, kan det hende at brukeren har funnet
            // en sårbarhet, eller at de sender sine egne meldinger; flagg dem.
            if(hintResult.getStatus() == HintResult.Status.INVALID_HINT){
                log.error("UserID (" + userID + "): Hintmelding med ugyldig indeks");
            }

            return;
        }

        String hint = hintResult.getHint();
        reply.setStatus("success");
        reply.setData(
            objectMapper.readTree("{\"hint\":\"" + hint + "\"}")
        );

        sender.send(userID, reply);
    }



    /**
     * Avbryter en oppgavesesjon
     * 
     * @param userID BrukerID
     */
    public void cancelTaskSession(UUID userID){
        taskSessionService.cancelTaskSession(userID);
    }



    /**
     * Initialiserer en rad i oppgavetabellen fra et oppgavefrø
     * 
     * @param seed Oppgavefrøet
     */
    public void createTask(TaskSeed seed){
        
        if(taskRepo.existsById(seed.getId())){
            log.info("OppgaveID (" + seed.getId() + ") eksisterer. Hopper over.");
            return;
        }
        
        Task task = new Task();
        task.setId(seed.getId());
        task.setTaskData(seed.getTaskData());
        task.setStaticFlag(seed.getStaticFlag());

        taskRepo.save(task);
        log.info("OppgaveID (" + seed.getId() + ") opprettet.");
    }



    public void hardFlushAllTasks(){
        taskRepo.deleteAll();
    }

}
