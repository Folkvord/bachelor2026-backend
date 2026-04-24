package no.bachelor26.Tasks.Hints.DTO;

import lombok.Data;

@Data
public class HintDTO {

    private String hint;
    private Short index;
    private Short cost;
    private boolean retrieved = false;  // Har hintet blitt brukt i oppgavesesjonen?

    public HintDTO(String hint, Short index, Short cost){
        this.hint = hint;
        this.index = index;
        this.cost = cost;
    }

}
