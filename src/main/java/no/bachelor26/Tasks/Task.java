package no.bachelor26.Tasks;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import no.bachelor26.Tasks.JSON.TaskData;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    private Long id;    // Vi skal ikke generere denne, den settes i oppgavefilene

    @Column(nullable = true)    // Testoppgaver / den siste oppgaven
    private Long unlocksTaskID;
    
    @Column(nullable = true)        // For de oppgavene som har et statisk flagg
    private String staticFlag;      // Dersom oppgaven har et dynamisk flagg, er denne kollonen null

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private TaskData taskData;

}
