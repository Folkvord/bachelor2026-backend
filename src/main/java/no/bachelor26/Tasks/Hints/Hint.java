package no.bachelor26.Tasks.Hints;

import jakarta.persistence.*;
import lombok.Data;
import no.bachelor26.Tasks.Task;

import java.time.Instant;


/**
 * Hintentitet for hinttabellen.
 * 
 * Representerer et hint tilknyttet en oppgave i systemet.
 *  
 * @author Sofie Emmelin Weber 
 */

@Data
@Entity
@Table(name = "hints")
public class Hint {

    @EmbeddedId
    private HintId id;

    // Kommentert ut for å løse N+1
    /* SOFIE: Setter slik at ikke et level har flere av hint 1, 2 og 3 */ 
/*     @MapsId("taskID")
    @ManyToOne(optional = false)
    @JoinColumn(name = "taskID", nullable = false)
    private Task task; */

    @Column(name = "hintMessage", nullable = false)
    private String hintMessage;

    @Column(nullable = false)
    private Short cost;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "updatedAt")
    private Instant updatedAt;

    public Hint(Integer taskID, Short index){
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