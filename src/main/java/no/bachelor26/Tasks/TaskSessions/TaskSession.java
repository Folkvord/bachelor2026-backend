package no.bachelor26.Tasks.TaskSessions;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;

@Getter
public class TaskSession {

    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;

    private String flag;
    private List<HintDTO> hints;


    public TaskSession(
        UUID userID,
        Long taskID,
        String flag,
        List<HintDTO> hints
    ){
        this.userID = userID;
        this.taskID = taskID;
        this.flag = flag;
        this.hints = hints;
        taskStart = LocalTime.now();
    }

}


