package no.bachelor26.Tasks.DTO;

import lombok.Data;
import no.bachelor26.Tasks.JSON.TaskData;

@Data
public class TaskProcessedResult {
    
    private String flag;
    private TaskData task;

    public TaskProcessedResult(String flag, TaskData task){
        this.flag = flag;
        this.task = task;
    }

}
