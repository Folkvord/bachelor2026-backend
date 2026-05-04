package no.bachelor26.Tasks.TaskSessions;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import no.bachelor26.Tasks.DTO.TaskComponents;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;

/**
 * En POJO som representerer en oppgavesesjon.
 * 
 * @author Kristoffer Folkvord
 */
@Getter
public class TaskSession {

    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;

    private String flag;
    private List<HintDTO> hints;
    private Long unlocksTaskID;

    public TaskSession(UUID userID, Long taskID, TaskComponents taskComponents){
        this.userID = userID;
        this.taskID = taskID;

        this.flag = taskComponents.getFlag();
        this.hints = taskComponents.getHints();
        this.unlocksTaskID = taskComponents.getUnlocksTaskId();
        
        taskStart = LocalTime.now();
    }

}


