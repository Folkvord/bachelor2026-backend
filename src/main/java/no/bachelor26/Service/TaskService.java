package no.bachelor26.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Entity.Task;
import no.bachelor26.Entity.User;
import no.bachelor26.Exception.TaskNotFoundException;
import no.bachelor26.Repository.TaskRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepo;


    public JsonNode getTaskJSONByName(String name){
        Task task = taskRepo.findByName(name).orElseThrow(
            () -> new TaskNotFoundException(name)
        );
        return convertToJsonNode(task.getTask());
    }


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
        return convertToJsonNode(jsonString);
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

    
    // Ment for å konvertere selve oppgaven fra en string til JSON
    private JsonNode convertToJsonNode(String str){
        return new ObjectMapper().readTree(str);
    }

}
