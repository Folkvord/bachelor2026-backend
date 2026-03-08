package no.bachelor26.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.bachelor26.Service.TaskService;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskService taskService;


    @GetMapping("/{name}")
    public ResponseEntity<JsonNode> getTask(@PathVariable String name){
        return ResponseEntity.ok(taskService.getTaskJSONByName(name));
    }




}