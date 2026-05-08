package no.bachelor26.User.Exception;


public class UserInTaskSessionException extends RuntimeException {
    
    public UserInTaskSessionException(Integer userID, Integer taskID){
        super("Brukeren med ID-en: " + userID.toString() + ", er allerede i en aktiv oppgave: " + taskID + ".");
    }

}
