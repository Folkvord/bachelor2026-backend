package no.bachelor26.WebSocket.Exception;

public class NoUserSessionException extends RuntimeException {
    
    public NoUserSessionException(){
        super();
    }

    public NoUserSessionException(String msg){
        super(msg);
    }

}
