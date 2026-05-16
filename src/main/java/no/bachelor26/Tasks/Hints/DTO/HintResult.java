package no.bachelor26.Tasks.Hints.DTO;

import lombok.Data;

/**
 * Et DTO / returnobjekt brukt for å
 * fortelle om statusen av hintetterspørslen 
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class HintResult {
    
    public enum Status{
        OK, INVALID_HINT, RETRIEVED
    }

    private String hint;
    private Status status;

}
