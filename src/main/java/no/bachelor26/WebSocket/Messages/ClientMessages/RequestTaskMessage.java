package no.bachelor26.WebSocket.Messages.ClientMessages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RequestTaskMessage {

    private Long taskID;

}
