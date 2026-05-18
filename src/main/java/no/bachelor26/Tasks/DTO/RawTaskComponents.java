package no.bachelor26.Tasks.DTO;

import lombok.Data;

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
    private Integer unlocksTaskID;

    public RawTaskComponents(TaskData data, String staticFlag, Integer unlocksTaskID){
        this.data = data;
        this.staticFlag = staticFlag;
        this.unlocksTaskID = unlocksTaskID;
    }

}
