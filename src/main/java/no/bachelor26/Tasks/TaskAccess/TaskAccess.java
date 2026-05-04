package no.bachelor26.Tasks.TaskAccess;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_task_access")
public class TaskAccess {

    @EmbeddedId
    private TaskAccessId id;
/* 
    // SOFIE: Kobler tilgang til en bruker /
    @ManyToOne(optional = false)
    @MapsId("userID")
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    // SOFIE: Kobler tilgang til one task /
    @ManyToOne(optional = false)
    @MapsId("taskID")
    @JoinColumn(name = "taskID", nullable = false)
    private Task task;
 */
    // SOFIE: Tidspunktet tasken ble låst opp /
    @Column(name = "unlockedAt", nullable = false, updatable = false)
    private Instant unlockedAt;

    public TaskAccess(){}

    public TaskAccess(UUID userID, Long taskID){
        id = new TaskAccessId(userID, taskID);
    }

    @PrePersist
    protected void onCreate() {
        unlockedAt = Instant.now();
    }
    
}