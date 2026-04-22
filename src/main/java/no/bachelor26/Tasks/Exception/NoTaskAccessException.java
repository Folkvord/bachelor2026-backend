package no.bachelor26.Tasks.Exception;

public class NoTaskAccessException extends RuntimeException {
    
    public NoTaskAccessException(String userID, String taskID){
        super("Brukeren med ID: " + userID + ", har ikke tilgang til oppgave: " + taskID);
    }

}
