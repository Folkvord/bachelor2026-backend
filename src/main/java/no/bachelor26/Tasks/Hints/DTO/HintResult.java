package no.bachelor26.Tasks.Hints.DTO;

import lombok.Data;

@Data
public class HintResult {
    
    public enum Status{
        OK, INVALID_HINT, RETRIEVED
    }

    private String hint;
    private Status status;

}
