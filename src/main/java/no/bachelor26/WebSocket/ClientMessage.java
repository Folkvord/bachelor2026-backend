package no.bachelor26.WebSocket;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class ClientMessage {
    
    private String type;
    private JsonNode data;

}
