package no.bachelor26.WebSocket.Messages.ServerMessages;


import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.bachelor26.WebSocket.Messages.ClientMessages.ClientMessage;

@Data
@EqualsAndHashCode(callSuper=false)
public class TaskInfoMessage extends ClientMessage {

    private Map<Long, TaskInfo> info;

    @Data
    public static class TaskInfo {
        private String name;
        private String description;
    }

}
