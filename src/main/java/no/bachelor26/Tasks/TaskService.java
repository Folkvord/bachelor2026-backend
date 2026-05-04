package no.bachelor26.Tasks;

import java.util.List;
import java.util.Optional;
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
import no.bachelor26.Tasks.TaskAccess.TaskAccessService;
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
    @Autowired TaskAccessService taskAccessService;

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
        result.setUnlocksTaskId(
            rawTaskComponents.getUnlocksTaskID()
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
        GameMessage reply = new GameMessage(msg);
        UUID userID = userSession.getUserID();

        List<Long> availableTaskIDs = taskAccessService.getAvailableTasks(userSession);
        reply.setData(
            objectMapper.valueToTree(
                availableTaskIDs
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
        GameMessage reply = new GameMessage(msg);
        UUID userID = userSession.getUserID();

        if(msg.getData() == null){
            sender.sendError(userID, reply, "invalid content");
            return;
        }

        // Er innholdet en long?
        JsonNode content = msg.getData().get("taskID");
        if(content == null || !content.canConvertToLong()){
            sender.sendError(userID, reply, "invalid content");
            return;
        }

        Long taskID = content.asLong();
        if(!taskAccessService.userHasAccess(userSession, taskID)){
            System.out.println("INGEN TILGANG ;(");
            sender.sendError(userID, reply, "no access");
            return;
        }

        // Hent prosessert oppgave
        TaskComponents taskComponents;
        try{
            taskComponents = getAndProcessTaskComponents(taskID);
        } catch(TaskNotFoundException e){
            sender.sendError(userID, reply, "no access");
            return;
        }

        // WTF SKAL SKJE DERSOM DET ER EN DUPE SESJON
        boolean startedSession = taskSessionService.startTaskSession(
            userID, taskID, taskComponents
        );

        if(!startedSession){
            sender.sendError(userID, reply, "dupe session");
            return;
        }

        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                taskComponents.getData()
            )
        );
        sender.send(userID, reply);

        userSession.changeState(UserState.PARSE_STANDBY);
    }



    /**
     * API-et som responderer til parsestatusmeldingen sendt etter klienten parser oppgaven.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToParseStatus(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage(msg);
        UUID userID = userSession.getUserID();

        if(msg.getStatus() == null){
            sender.sendError(userID, reply, "invalid content");
            return;
        }

        boolean userInSession = taskSessionService.userInActiveSession(userID);
        if(!userInSession){
            userSession.changeState(UserState.IDLE);
            sender.sendError(userID, reply, "no session");
            return;
        }

        reply.setStatus("success");
        switch(msg.getStatus()){
            case "success":
                userSession.changeState(UserState.ACTIVE_TASK);
                break;

            case "error":
                taskSessionService.cancelSession(userID);
                userSession.changeState(UserState.IDLE);
                break;

            default:
                userSession.changeState(UserState.IDLE);
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
        
        if(msg.getStatus() == null){
            log.error("bro");
        }
        else if(msg.getStatus().equals("error")){
            
            // Hvis en beskrivelse i datafeltet
            if(msg.getData() != null && msg.getData().has("desc")){    // Tilgi meg for denne drittkoden
                String reason = msg.getData().get("desc").asString();
                log.error("UserID (" + userID + "): Avbrøt oppgavesesjonen sin på grunn av en feil: " + reason + ".");
            }
            else{
                log.error("UserID (" + userID + "): Avbrøt oppgavesesjonen sin på grunn av en ukjent feil.");
            }

        }

        taskSessionService.cancelSession(userID);
        userSession.changeState(UserState.IDLE);
    }



    /**
     * API-et for å validere et flagg.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToValidateFlag(UserSession userSession, GameMessage msg){
        GameMessage reply = new GameMessage(msg);
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

            Long unlockedID = taskSessionService.completeSession(userID);
            if(unlockedID != null){     // Det er mulig at en oppgave ikke låser opp en annen
                taskAccessService.grantUserAccess(userID, unlockedID);
            }

            userSession.changeState(UserState.IDLE);
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
        GameMessage reply = new GameMessage(msg);
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
        taskSessionService.cancelSession(userID);
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
        
        if(seed.getUnlocks() == null){
            log.warn("OppgaveID (" + seed.getId() + ") er satt til å ikke gi tilgang til en annen oppgave.");
        }
        task.setUnlocksTaskID(seed.getUnlocks());
        task.setTaskData(seed.getTaskData());
        task.setStaticFlag(seed.getStaticFlag());

        taskRepo.save(task);
        log.info("OppgaveID (" + seed.getId() + ") opprettet.");
    }



    /**
     * Redigerer en rad i oppgavetabellen dersom den finnes, eller skaper den dersom ikke
     * 
     * @param seed Oppgavefrøet
     */
    public void editOrCreateTask(TaskSeed seed, boolean skipIfPresent){
        Optional<Task> possibleTask = taskRepo.findById(seed.getId());
        
        Task task;
        String actionTaken = "none";
        if(possibleTask.isPresent() && skipIfPresent){
            log.info("OppgaveID (" + seed.getId() + ") eksisterer. Hopper over.");
            return;
        }
        else if(possibleTask.isPresent()){
            task = possibleTask.get();
            actionTaken = "redigert.";
        }
        else{
            task = new Task(seed.getId());
            actionTaken = "opprettet.";
        }

        task.setTaskData(seed.getTaskData());
        task.setStaticFlag(seed.getStaticFlag());
        if(task.getUnlocksTaskID() == null){
            log.warn("OppgaveID (" + seed.getId() + ") er satt til å ikke gi tilgang til en annen oppgave.");
        }
        task.setUnlocksTaskID(seed.getUnlocks());

        taskRepo.save(task);
        log.info("OppgaveID (" + seed.getId() + ") " + actionTaken);
    }



    public void hardFlushAllTasks(){
        taskRepo.deleteAll();
    }

}
