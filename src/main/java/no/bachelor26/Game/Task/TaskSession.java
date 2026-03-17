package no.bachelor26.Game.Task;

import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import no.bachelor26.WebSocket.WebSocketSender;
import no.bachelor26.WebSocket.Game.GameMessage;

public class TaskSession {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired 
    WebSocketSender message;
    
    private final int TICK_MS = 250;

    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;

    private BlockingQueue<GameMessage> messageQueue = new LinkedBlockingDeque<>();
    public Future<?> future;

    private TaskState currentState = TaskState.STANDBY;


    public TaskSession(UUID userID, Long taskID){
        this.userID = userID;
        this.taskID = taskID;
        taskStart = LocalTime.now();
    }


    public void forwardMessage(GameMessage msg){
        messageQueue.offer(msg);
    }


    /**
     * Kjører oppgavesesjonen.
     * Funksjonen henter en ny melding hvert {@value #TICK_MS}ms fra meldingskøen
     * og håndterer den så lenge tilstanden til sesjonen ikke er TaskState.STOPPED.
     * 
     * @author Kristoffer Folkvord 
     */
    public void run(){
        
        while(currentState != TaskState.STOPPED){

            // Hent meldingen
            GameMessage msg;
            try{
                msg = messageQueue.poll(TICK_MS, TimeUnit.MILLISECONDS);
            } catch(InterruptedException e){
                future.cancel(true);
                log.error("Oppgavesesjon ble avbrytet.");
                return;
            }

            if(currentState == TaskState.STANDBY){
                handleStandbyMessage(msg);
            }
            else if(currentState == TaskState.RUNNING){
                handleMessage(msg);
            }

        }
    }





    private void handleMessage(GameMessage msg){

        switch(msg.getType()){
            case "cancel":
                
                break;
            case "validate flag":
                
                break;

        
            default:
                break;
        }

    }


    /**
     * Håndterer meldingstyper serveren forventer i standby-tilstanden.
     * 
     * @param msg GameMessage-objektet med meldingen
     */
    private void handleStandbyMessage(GameMessage msg){
        
        if(!msg.getType().equals("parse status")){
            log.warn("Oppgavesesjon i standby fikk en ugyldig meldingstype:" + msg.getType());
            return;
        }

        switch(msg.getStatus()){

            case "success":
                currentState = TaskState.RUNNING;
                break;
            case "error":
                currentState = TaskState.STOPPED;
                log.error("En klient: (" + userID + ") kunne ikke parse oppgaven med ID: (" + taskID + ").");
                break;
            default:
                log.error("En klient: (" + userID + ") fikk en ukjent status: " + msg.getStatus());
                break;

        }

    }


}


enum TaskState{
    STANDBY,
    RUNNING,
    STOPPED
}