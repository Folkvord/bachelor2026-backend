package no.bachelor26.Tasks.DTO;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class TaskProcessedResult {
    
    private String flag;
    private JsonNode task;

    public TaskProcessedResult(String flag, JsonNode task){
        this.flag = flag;
        this.task = task;
    }

}
