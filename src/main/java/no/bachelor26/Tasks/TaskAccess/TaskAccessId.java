package no.bachelor26.Tasks.TaskAccess;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * Primørnøkkel for entiteten user_tasl_access
 * 
 * Sammensatt nøkkel fra userID og taskID
 *  
 * @author Sofie Emmelin Weber 
 */

@Data
@Embeddable
public class TaskAccessId implements Serializable {

    @Column(name = "userID")
    private Integer userID;

    @Column(name = "taskID")
    private Integer taskID;

    public TaskAccessId(){}

    public TaskAccessId(Integer userID, Integer taskID){
        this.userID = userID;
        this.taskID = taskID;
    }
    
}