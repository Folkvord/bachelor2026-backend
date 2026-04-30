package no.bachelor26.WebSocket.Exception;

public class DupeUserSessionException extends RuntimeException {
    
    public DupeUserSessionException(){
        super();
    }

    public DupeUserSessionException(String msg){
        super(msg);
    }

}
