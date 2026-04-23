package no.bachelor26.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.DTO.TaskProcessedResult;
import no.bachelor26.Tasks.Exception.TaskNotFoundException;
import no.bachelor26.Tasks.JSON.TaskData;
import no.bachelor26.Tasks.TaskSession.TaskState;
import no.bachelor26.User.UserSession;
import no.bachelor26.User.UserState;
import no.bachelor26.WebSocket.WebSocketSender;
import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;


@Service
public class TaskService {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebSocketSender sender;

    @Autowired
    TaskProcesser taskProcesser;


    Map<UUID, TaskSession> activeSessions = new ConcurrentHashMap<>();



    /**
     * Henter innholdet til en oppgave (for sjapp debugging).
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Innholdet
     * @author Kristoffer Folkvord
     */
    public TaskData getTaskById(Long id){
        TaskComponents unprocessedTask = taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
        return taskProcesser.process(unprocessedTask).getTask();
    }



    /**
     * Henter innholdet til en oppgave.
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Innholdet og det statiske flagget om et finnes
     * @author Kristoffer Folkvord
     */
    public TaskComponents getUnprocessedTaskContentById(Long id){
        return taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
    }



    /**
     * Henter og prosesserer innholdet til en oppgave.
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Det prosesserte innholdet til en oppgave og flagget
     * @author Kristoffer Folkvord
     */
    public TaskProcessedResult getAndProcessTaskComponents(Long id) throws TaskNotFoundException{
        TaskComponents unprocessedTask = taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
        return taskProcesser.process(unprocessedTask);
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
     * @author Kristoffer Folkvord
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
        TaskProcessedResult processedTask;
        try{
            processedTask = getAndProcessTaskComponents(taskID);
        } catch(TaskNotFoundException e){
            sender.sendError(userID, reply, "no access");
            return;
        }

        // WTF SKAL SKJE DERSOM DET ER EN DUPE SESJON
        boolean startedSession = startTaskSession(userID, taskID, processedTask.getFlag());
        if(!startedSession){
            sender.sendError(userID, reply, "dupe session");
            return;
        }

        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                processedTask.getTask()
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

        TaskSession taskSession = getUserTaskSession(userID);
        if(taskSession == null){
            userSession.setState(UserState.IDLE);
            sender.sendError(userID, reply, "no session");
            return;
        }

        if(!taskSession.inStandby()){
            userSession.setState(UserState.IDLE);
            sender.sendError(userID, reply, "invalid session state");
            return;
        }

        reply.setStatus("success");
        switch(msg.getStatus()){
            case "success":
                taskSession.setCurrentState(TaskState.RUNNING);
                userSession.setState(UserState.ACTIVE_TASK);
                break;

            case "error":
                taskSession.setCurrentState(TaskState.STOPPED);
                userSession.setState(UserState.IDLE);
                cancelTaskSession(userID);
                break;

            default:
                taskSession.setCurrentState(TaskState.STOPPED);
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
        
        if(msg.getStatus().equals("error")){
            System.out.println(" avbrytelse");
        }

        cancelTaskSession(userSession.getUserID());
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

        System.out.println("FLAGG");
        if(!activeSessions.containsKey(userID)){
            sender.sendError(userID, reply, "no session");
            return;
        }

        if(msg.getData() == null || !msg.getData().has("flag")){
            sender.sendError(userID, reply, "invalid data format");
            return;
        }

        TaskSession taskSession = getUserTaskSession(userID);
        if(!taskSession.isRunning()){
            sender.sendError(userID, reply, "invalid task state");
            return;
        }

        String flag = "wiener";
        try{
            flag = msg.getData().get("flag").asString();
        } catch(JsonNodeException e){
            sender.sendError(userID, reply, "invalid flag format");
            return;
        }

        String result = taskSession.validateFlag(flag) ? "correct" : "wrong";
        if(result.equals("correct")){
            userSession.setState(UserState.IDLE);
        }

        reply.setStatus("success");
        reply.setData(
            objectMapper.readTree("{\"result\":\"" + result + "\"}")
        );

        sender.send(userID, reply);
    }

    
    
    /**
     * Starter en ny oppgavesesjon hvis ikke en allerede eksisterer.
     * 
     * @param userID ID-en til klienten som starter sesjonen
     * @param taskID ID-en til oppgaven sesjonen starter med
     */
    public boolean startTaskSession(UUID userID, Long taskID, String flag){
        if(activeSessions.containsKey(userID)){
            return false;
        }

        activeSessions.put(userID, new TaskSession(
            userID, taskID, flag
        ));

        return true;
    }



    /**
     * Avbryter en oppgavesesjon ved å fjerne den fra activeSessions.
     * 
     * @param userID ID-en til klientens oppgavesesjon
     * @return 
     */
    public boolean cancelTaskSession(UUID userID){
        return activeSessions.remove(userID) != null;
    }



    /**
     * Undersøker om en spiller er i en aktiv oppgavesesjon.
     * 
     * @param userID ID-en til klienten som undersøkes
     * @return En boolean som svarer på spørsmålet, duh
     * @author Kristoffer Folkvord
     */
    public boolean userInTaskSession(UUID userID){
        return activeSessions.containsKey(userID);
    }



    /**
     * Gir taskSession-objektet assisiert med en klient 
     * 
     * @param userID ID-en til klienten som undersøkes
     * @return TaskSession-objektet, eller null om klienten ikke er i en aktiv sesjon
     * @author Kristoffer Folkvord
     */
    public TaskSession getUserTaskSession(UUID userID){
        return activeSessions.get(userID);
    }



    
    public void createTask(Long id, Map<String, Object> data, List<String> hints){

        if(taskRepo.existsById(id)){
            log.info("OppgaveID (" + id + ") eksisterer fra før av; hopper over.");
            return;
        }

        Task task = new Task();
        task.setId(id);
        


        task.setTaskData(null);

    }


    
}
