package no.bachelor26.WebSocket.Exception;

public class UnexpectedSessionShutdownException extends RuntimeException {
    
    public UnexpectedSessionShutdownException(){
        super();
    }

    public UnexpectedSessionShutdownException(String msg){
        super(msg);
    }

}
