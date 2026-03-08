package no.bachelor26.Exception;

public class EmailInUseException extends RuntimeException {

    public EmailInUseException(String email){
        super("Eposten '" + email + "' er allerede i bruk");
    }

}
