package no.bachelor26.Tasks.Exception;

public class NoActiveTaskSessionException extends RuntimeException {
    
    public NoActiveTaskSessionException(String msg){
        super(msg);
    }

    public NoActiveTaskSessionException(){
        super();
    }


}
