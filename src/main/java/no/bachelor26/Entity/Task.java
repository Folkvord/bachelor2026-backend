package no.bachelor26.Entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import no.bachelor26.Game.Datatypes.TaskJSON;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    private String name;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private TaskJSON taskJSON;

}
