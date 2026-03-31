package no.bachelor26.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)        // For de oppgavene som har et statisk flagg
    private String staticFlag;      // Dersom oppgaven har et dynamisk flagg, er denne kollonen null

    @Column(columnDefinition = "jsonb")
    private String taskData;

}
