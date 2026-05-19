package no.bachelor26.Tasks;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import no.bachelor26.Tasks.DTO.TaskData;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    private Integer id;    // Vi skal ikke generere denne, den settes i oppgavefilene

    @Column
    private Integer unlocksTaskID;
    
    @Column(nullable = true)        // For de oppgavene som har et statisk flagg
    private String staticFlag;      // Dersom oppgaven har et dynamisk flagg, er denne kollonen null

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private TaskData taskData;

    public Task(Integer taskID){
        this.id = taskID;
    }

    public Task(){}

}