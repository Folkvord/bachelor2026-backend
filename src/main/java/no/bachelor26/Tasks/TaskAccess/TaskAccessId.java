package no.bachelor26.Tasks.TaskAccess;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class TaskAccessId implements Serializable {

    @Column(name = "userID", columnDefinition = "uuid")
    private UUID userID;

    @Column(name = "taskID")
    private Long taskID;

    public TaskAccessId(){}

    public TaskAccessId(UUID userID, Long taskID){
        this.userID = userID;
        this.taskID = taskID;
    }
    
}