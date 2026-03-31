package no.bachelor26.Exception;

import java.util.UUID;

public class UserInTaskSessionException extends RuntimeException {
    
    public UserInTaskSessionException(UUID userID, Long taskID){
        super("Brukeren med ID-en: " + userID.toString() + ", er allerede i en aktiv oppgave: " + taskID + ".");
    }

}
