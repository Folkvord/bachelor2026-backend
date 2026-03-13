package no.bachelor26.WebSocket;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class ServerMessage {
    
    private String type;
    private String status;
    private JsonNode data;
    
}
