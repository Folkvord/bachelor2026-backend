package no.bachelor26.Tasks.DTO;

import java.util.List;

import lombok.Getter;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.JSON.TaskData;

@Getter
public class TaskSeed {
    
    private Long id;
    private TaskData taskData;
    private List<HintDTO> hints;

}
