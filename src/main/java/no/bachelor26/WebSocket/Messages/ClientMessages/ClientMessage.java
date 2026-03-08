package no.bachelor26.WebSocket.Messages.ClientMessages;

import lombok.Data;

@Data
public abstract class ClientMessage {
    
    private String type;

}
