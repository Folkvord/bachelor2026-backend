package no.bachelor26.Tasks.Hints;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class HintId implements Serializable {
    
    @Column(nullable = false)
    private Long taskID;
    
    @Column(nullable = false)
    private Short index;

    public HintId(Long taskID, Short index){
        this.taskID = taskID;
        this.index = index;
    }

    public HintId(){}

}
