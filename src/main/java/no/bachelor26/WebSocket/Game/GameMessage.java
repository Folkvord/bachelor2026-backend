package no.bachelor26.WebSocket.Game;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class GameMessage {

    private String type;
    private int requestID;
    private String status;
    private JsonNode data;
    
    public GameMessage(String type){
        this.type = type;
    }

}
