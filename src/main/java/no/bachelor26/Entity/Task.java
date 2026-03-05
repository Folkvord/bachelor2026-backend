package no.bachelor26.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    private Long id;

    private String name;
    private String description;

    @Column(columnDefinition = "jsonb")
    private String task;

}
