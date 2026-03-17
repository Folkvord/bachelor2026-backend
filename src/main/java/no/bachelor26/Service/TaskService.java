package no.bachelor26.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Entity.Task;
import no.bachelor26.Entity.User;
import no.bachelor26.Exception.TaskNotFoundException;
import no.bachelor26.Exception.UserInTaskSessionException;
import no.bachelor26.Game.Task.TaskSession;
import no.bachelor26.Repository.TaskRepository;
import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class TaskService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    ExecutorService executorService;

    Map<UUID, TaskSession> activeSessions = new ConcurrentHashMap<>();


    /**
     * Henter innholdet til en oppgave.
     * 
     * @param id ID-en til oppgaven som skal hentes
     * @return Innholdet
     * @author Kristoffer Folkvord
     */
    public JsonNode getTaskContentById(Long id){
        String jsonString = taskRepo.findTaskContentById(id).orElseThrow(
            () -> new TaskNotFoundException(id.toString())
        );
        return new ObjectMapper().readTree(jsonString);
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

    

    public void startTaskSession(UUID userID, Long taskID){
        if(activeSessions.containsKey(userID)){
            log.warn("En bruker i en aktiv oppgavesesjon prøvde å starte en ny oppgavesesjon");
            throw new UserInTaskSessionException(userID, taskID);
        }

        TaskSession taskSession = new TaskSession(userID, taskID);
        activeSessions.put(userID, taskSession);
        executorService.submit(taskSession::run);
    }


    /**
     * Lukker en oppgavesesjon dersom i en. 
     * Gjør ingenting dersom brukeren ikke er i en.
     * 
     * @param userID ID-en til brukeren
     */
    public void closeTaskSession(UUID userID){
        if(userInTaskSession(userID)){
            activeSessions.remove(userID);
        }
    }

    
    /**
     * Videresender en melding til en klient i en aktiv oppgavesesjon.
     * 
     * @param userID ID-en på brukeren som skal ha meldingen
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    public void forwardMessageToTaskSession(UUID userID, GameMessage msg){       // Kan hende det fucker opp i fremtiden; gjør evt sjekker på om den er i mappet
        activeSessions.get(userID).forwardMessage(msg);
    }


    /**
     * Undersøker om en spiller er i en aktiv oppgavesesjon.
     * 
     * @param userID ID-en til brukeren som undersøkes
     * @return En boolean som svarer på spørsmålet, duh
     * @author Kristoffer Folkvord
     */
    public boolean userInTaskSession(UUID userID){
        return activeSessions.containsKey(userID);
    }

}
