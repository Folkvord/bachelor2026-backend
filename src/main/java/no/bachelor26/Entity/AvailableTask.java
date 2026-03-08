package no.bachelor26.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Entitet for å vise at en oppgave er tigjengelig for en bruker.
 * 
 * @author Kristoffer Folkvord
 */
@Data
@Entity
@Table(name="available_task")
public class AvailableTask {

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name="task_id", referencedColumnName = "id")    
    private Task task;

}
