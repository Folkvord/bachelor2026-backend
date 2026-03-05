package no.bachelor26.Exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String taskName){
        super("Oppgaven med navn: '" + taskName + "', kunne ikke finnes.");
    }

}
