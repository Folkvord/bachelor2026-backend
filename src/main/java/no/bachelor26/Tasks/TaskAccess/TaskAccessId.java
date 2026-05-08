package no.bachelor26.Tasks.TaskAccess;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

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