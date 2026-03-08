package no.bachelor26.Exception;

public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username){
        super("Brukernavnet '" + username + "' er allerede tatt");
    }

}
