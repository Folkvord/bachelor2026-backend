package no.bachelor26.Tasks.Hints;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import no.bachelor26.Tasks.Task;

@Data
@Entity
@Table(name = "hints",
       uniqueConstraints = @UniqueConstraint(columnNames = {"taskID", "hintIndex"}))
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hintId;

    /* SOFIE: Setter slik at ikke et level har flere av hint 1, 2 og 3 */ 
    @ManyToOne(optional = false)
    @JoinColumn(name = "taskID", nullable = false)
    private Task task;

    /* SOFIE: Setter hintindex, melding, og kostnaden */
    @Column(name = "hintIndex", nullable = false)
    private Short hintIndex;

    @Column(name = "hintMessage", nullable = false)
    private String hintMessage;

    @Column(nullable = false)
    private Short cost;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "updatedAt")
    private Instant updatedAt;

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