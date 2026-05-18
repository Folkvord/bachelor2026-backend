package no.bachelor26.Tasks.DTO;

import java.util.List;

import lombok.Data;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;

/**
 * En POJO som inneholder alle komponentene
 * nødvending for starten av en oppgave
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class TaskComponents {

    private TaskData data;
    private String flag;
    private List<HintDTO> hints;
    private Integer unlocksTaskId;

}
