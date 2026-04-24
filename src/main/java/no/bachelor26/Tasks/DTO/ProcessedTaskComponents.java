package no.bachelor26.Tasks.DTO;

import java.util.List;

import lombok.Data;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.JSON.TaskData;

/**
 * En POJO som inneholder alle de prosesserte komponentene
 * nødvending for starten av en oppgave
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class ProcessedTaskComponents {

    private TaskData data;
    private String flag;
    private List<HintDTO> hints;

}
