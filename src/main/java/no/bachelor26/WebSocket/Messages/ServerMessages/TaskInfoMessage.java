package no.bachelor26.WebSocket.Messages.ServerMessages;


import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.bachelor26.Projection.TaskInfo;
import no.bachelor26.WebSocket.Messages.ClientMessages.ClientMessage;

@Data
@EqualsAndHashCode(callSuper=false)
public class TaskInfoMessage extends ClientMessage {

    private List<TaskInfo> taskInfo;

    public TaskInfoMessage(List<TaskInfo> taskInfo){
        this.taskInfo = taskInfo;
    }

}
