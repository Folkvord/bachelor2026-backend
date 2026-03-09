package no.bachelor26.Exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String taskName){
        super("Oppgaven med navn: '" + taskName + "', finnes ikke.");
    }

    public TaskNotFoundException(Long taskID){
        super("Oppgaven med ID: '" + taskID + "', finnes ikke.");
    }
    
}
