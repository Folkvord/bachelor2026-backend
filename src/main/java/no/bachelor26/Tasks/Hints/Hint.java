package no.bachelor26.Tasks.Hints;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "hints")
public class Hint {

    @EmbeddedId
    private HintId id;

    /* SOFIE: Setter slik at ikke et level har flere av hint 1, 2 og 3 */ 
/*     @ManyToOne(optional = false)
    @JoinColumn(name = "taskID", nullable = false)
    private Task task;
 */
    @Column(name = "hintMessage", nullable = false)
    private String hintMessage;

    @Column(nullable = false)
    private Short cost;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "updatedAt")
    private Instant updatedAt;

    public Hint(Long taskID, Short index){
        id = new HintId(taskID, index);
    }

    public Hint(HintId id){
        this.id = id;
    }
    
    public Hint(){}

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        }
}