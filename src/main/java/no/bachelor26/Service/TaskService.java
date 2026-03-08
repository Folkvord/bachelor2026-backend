package no.bachelor26.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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





    public void grantTaskAccess(User user, Long taskID){
        
    }

    
    // Ment for å konvertere selve oppgaven fra en string til JSON
    private JsonNode convertToJsonNode(String str){
        return new ObjectMapper().readTree(str);
    }

}
