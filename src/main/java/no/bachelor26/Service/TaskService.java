package no.bachelor26.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.DTO.TaskProcessedResult;
import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Entity.Task;
import no.bachelor26.Entity.User;
import no.bachelor26.Exception.TaskNotFoundException;
import no.bachelor26.Exception.UserInTaskSessionException;
import no.bachelor26.Game.Task.TaskProcesser;
import no.bachelor26.Game.Task.TaskSession;
import no.bachelor26.Game.Task.TaskSession.TaskState;
import no.bachelor26.Projection.TaskComponents;
import no.bachelor26.Repository.TaskRepository;
import no.bachelor26.WebSocket.WebSocketSender;
import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class TaskService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    AvailableTaskService availableTaskService;

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
    public JsonNode getTaskById(Long id){
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
     * Gir en bruker tilgang til en oppgave.
     * 
     * @param user Brukeren som skal få tilgang
     * @param taskID ID-en til oppgaven brukeren skal få tilgang til
     * @throws TaskNotFoundException
     * @author Kristoffer Folkvord
    */
    public void grantTaskAccess(User user, Long taskID){
        AvailableTask availableTaskToken = new AvailableTask();
        availableTaskToken.setUser(user);

        Task task = taskRepo.findById(taskID).orElseThrow(
            () -> new TaskNotFoundException(taskID)
        );

        availableTaskToken.setTask(task);
    }



    /**
     * API-et for henting av oppgaveinformasjon.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @author Kristoffer Folkvord
     */
    public void respondToTaskInfo(UUID userID){
        GameMessage reply = new GameMessage("task-info");

        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                availableTaskService.getAvailableTaskInfo(userID)
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
    public void respondToTask(UUID userID, JsonNode data){
        GameMessage reply = new GameMessage("task");

        // Er innholdet en long?
        JsonNode content = data.get("taskID");
        if(content == null || !content.canConvertToLong()){
            sender.sendError(userID, reply, "invalid content");
            return;
        }

        Long taskID = content.asLong();

        // Har brukeren tilgang til oppgaven?
        if(!availableTaskService.userHasAccessToTask(userID, taskID)){
            sender.sendError(userID, reply, "no access");
            return;
        }
        
        // Hent prosessert oppgave
        TaskProcessedResult processedTask;
        try{
            processedTask = getAndProcessTaskComponents(taskID);
        } catch(TaskNotFoundException e){
            sender.sendError(userID, reply, "no access");
            return;
        }

        reply.setData(
            processedTask.getTask()
        );

        reply.setStatus("success");
        sender.send(userID, reply);
        startTaskSession(userID, taskID, processedTask.getFlag());
    }



    /**
     * API-et som responderer til parsestatusmeldingen sendt etter klienten parser oppgaven.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToParseStatus(UUID userID, GameMessage msg){
        GameMessage reply = new GameMessage("parse-status");

        TaskSession session = getUserTaskSession(userID);
        if(session == null){
            log.error("Klienten: (" + userID + "), som ikke er i en aktiv oppgavesesjon prøvde å avbryte en");
            sender.sendError(userID, reply, "no session");
            return;
        }

        if(session.getCurrentState() != TaskState.STANDBY){
            log.error("Klienten: (" + userID + "), sendte en parse-status melding til en oppgavesesjon med en taskstate ulik STANDBY");
            sender.sendError(userID, reply, "invalid session state");
            return;
        }

        reply.setStatus("success");
        switch(msg.getStatus()){
            case "success":
                session.setCurrentState(TaskState.RUNNING);
                break;

            case "error":
                session.setCurrentState(TaskState.STOPPED);
                log.error("En klient: (" + userID + ") kunne ikke parse oppgaven med ID: (" + session.getTaskID() + "), gitt grunn: " + msg.getData());
                respondToCancelTask(userID);
                break;

            default:
                session.setCurrentState(TaskState.STOPPED);
                log.error("En klient: (" + userID + ") fikk en ukjent status: " + msg.getStatus());
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
    public void respondToCancelTask(UUID userID){
        if(!activeSessions.containsKey(userID)){
            log.error("Klienten: (" + userID + "), som ikke er i en aktiv oppgavesesjon prøvde å avbryte en");
            return;
        }
        log.info("Klienten: (" + userID + "), avbrøt oppgavesesjonen sin");
        activeSessions.remove(userID);
    }



    /**
     * API-et for å validere et flagg.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg GameMessage
     */
    public void respondToValidateFlag(UUID userID, GameMessage msg){
        GameMessage reply = new GameMessage("cancel-task");

        if(!activeSessions.containsKey(userID)){
            log.warn("Klienten: (" + userID + "), som ikke er i en aktiv oppgavesesjon prøvde å validere et flagg");
            sender.sendError(userID, reply, "no session");
            return;
        }

        if(msg.getData() == null || !msg.getData().has("flag")){
            log.error("Klienten: (" + userID + "), sendte en ugyldig validate-flag melding");
            sender.sendError(userID, reply, "invalid format");
            return;
        }

        TaskSession session = getUserTaskSession(userID);
        if(session.getCurrentState() != TaskSession.TaskState.RUNNING){
            log.error("Klienten: (" + userID + "), prøvde å validere et flagg før parsingen av oppgaven var ferdig");
            sender.sendError(userID, msg, "invalid task state");
            return;
        }

        String flag = msg.getData().get("flag").toString();
        System.out.println(flag);

        log.info("Klienten: (" + userID + "), validerte et flagg");
        
        String result = session.validateFlag(flag) ? "correct" : "wrong";
        reply.setStatus("success");
        reply.setData(
            objectMapper.readTree("{\"result\":\"" + result + "\"}")
        );

        sender.send(userID, msg);
    }


    
    /**
     * Starter en ny oppgavesesjon
     * 
     * @param userID ID-en til klienten som starter sesjonen
     * @param taskID ID-en til oppgaven sesjonen starter med
     */
    public void startTaskSession(UUID userID, Long taskID, String flag){
        if(activeSessions.containsKey(userID)){
            log.error("En bruker i en aktiv oppgavesesjon prøvde å starte en ny oppgavesesjon");
            throw new UserInTaskSessionException(userID, taskID);
        }

        TaskSession taskSession = new TaskSession(userID, taskID, flag);
        activeSessions.put(userID, taskSession);
        log.info("KLIENT STARTET SESJON");
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


    
}
