package no.bachelor26.Tasks.DTO;

import lombok.Data;
import no.bachelor26.Tasks.JSON.TaskData;

/**
 * En POJO for å hente ut de nødvendige komponentene 
 * for en oppgave før prosesseringen
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class RawTaskComponents {

    private TaskData data;
    private String staticFlag;

    public RawTaskComponents(TaskData data, String staticFlag){
        this.data = data;
        this.staticFlag = staticFlag;
    }

}
