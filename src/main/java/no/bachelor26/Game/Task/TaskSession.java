package no.bachelor26.Game.Task;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;
import no.bachelor26.Service.TaskService;
import no.bachelor26.WebSocket.WebSocketSender;

@Data
public class TaskSession {

    @Autowired 
    WebSocketSender message;

    @Autowired 
    TaskService taskService;


    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;

    private TaskState currentState = TaskState.STANDBY;

    public TaskSession(UUID userID, Long taskID){
        this.userID = userID;
        this.taskID = taskID;
        taskStart = LocalTime.now();
    }
    
    public enum TaskState{
        STANDBY,
        RUNNING,
        STOPPED
    }

}


