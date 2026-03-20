package no.bachelor26.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Entity.Task;
import no.bachelor26.Entity.User;
import no.bachelor26.Exception.TaskNotFoundException;
import no.bachelor26.Exception.UserInTaskSessionException;
import no.bachelor26.Game.Task.TaskProcesser;
import no.bachelor26.Game.Task.TaskSession;
import no.bachelor26.Projection.TaskContent;
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
        TaskContent unprocessedTask = taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
        return taskProcesser.preprocess(unprocessedTask);
    }



    /**
     * Henter innholdet til en oppgave.
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Innholdet
     * @author Kristoffer Folkvord
     */
    public TaskContent getUnprocessedTaskContentById(Long id){
        return taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
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
     * Starter en ny oppgavesesjon
     * 
     * @param userID ID-en til klienten som starter sesjonen
     * @param taskID ID-en til oppgaven sesjonen starter med
     */
    public void startTaskSession(UUID userID, Long taskID){
        if(activeSessions.containsKey(userID)){
            log.warn("En bruker i en aktiv oppgavesesjon prøvde å starte en ny oppgavesesjon");
            throw new UserInTaskSessionException(userID, taskID);
        }

        TaskSession taskSession = new TaskSession(userID, taskID);
        activeSessions.put(userID, taskSession);
    }
    
    

    /**
     * API-et for henting av oppgaveinformasjon.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @author Kristoffer Folkvord
     */
    public void respondToTaskInfo(UUID userID){
        GameMessage reply = new GameMessage("task-info");
        
        if(userID == null){
            sender.sendError(userID, reply, "no userID");
            return;
        }
        
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

        // Sjekk om noe fandango har skjedd med brukerID-en
        if(userID == null){
            sender.sendError(userID, reply, "no userID");
            return;
        }

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
        
        // Oppgaven finnes ikke sant?
        TaskContent taskContent;
        try{
            taskContent = getUnprocessedTaskContentById(taskID);
        } catch(TaskNotFoundException e){
            sender.sendError(userID, reply, "no access");
            return;
        }
        
        reply.setData(
            taskProcesser.preprocess(taskContent)
        );

        reply.setStatus("success");
        sender.send(userID, reply);

        startTaskSession(userID, taskID);
    }



    /**
     * API-et som responderer til parsestatusmeldingen sendt etter klienten parser oppgaven.
     * 
     * @param msg GameMessage
     */
    public void respondToParseStatus(UUID userID, GameMessage msg){
        TaskSession session = getUserTaskSession(userID);
        if(session == null){
            //throw new UserInNoTaskSessionException(userID);
        }

        switch(msg.getStatus()){
            case "success":
                session.setCurrentState(TaskSession.TaskState.RUNNING);
                break;

            case "error":
                session.setCurrentState(TaskSession.TaskState.STOPPED);
                log.error("En klient: (" + userID + ") kunne ikke parse oppgaven med ID: (" + session.getTaskID() + ").");
                break;

            default:
                log.error("En klient: (" + userID + ") fikk en ukjent status: " + msg.getStatus());
                break;
        }
    }



    /**
     * API-et for å avbryte en oppgavesesjon.
     * 
     * @param userID ID-en på klienten som kaller API-et
     * @param msg
     */
    public void respondToCancelTask(UUID userID, GameMessage msg){
        if(!activeSessions.containsKey(userID)){
            log.warn("Klienten: (" + userID + "), som ikke er i en aktiv oppgavesesjon prøvde å avbryte en");
            return;
        }
        log.info("Klienten: (" + userID + "), avbrøt oppgavesesjonen sin");
        activeSessions.remove(userID);
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
