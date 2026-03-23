package no.bachelor26.Game.Task;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;
import no.bachelor26.WebSocket.WebSocketSender;

@Data
public class TaskSession {

    @Autowired 
    WebSocketSender message;


    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;
    private String flag;

    private TaskState currentState = TaskState.STANDBY;

    public TaskSession(UUID userID, Long taskID, String flag){
        this.userID = userID;
        this.taskID = taskID;
        this.flag = flag;
        taskStart = LocalTime.now();
    }
    


    // Midlertidig validering
    public boolean validateFlag(String flag){
        return flag.equals(this.flag);
    }



    public boolean inStandby(){
        return currentState == TaskState.STANDBY;
    }



    public boolean isRunning(){
        return currentState == TaskState.RUNNING;
    }



    public enum TaskState{
        STANDBY,
        RUNNING,
        STOPPED
    }

}


