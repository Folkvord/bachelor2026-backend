package no.bachelor26.WebSocket.Game;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Data
public class GameMessage {
    
    @Autowired
    ObjectMapper objectMapper;

    private String type;
    private String status;
    private JsonNode data;
    

    public GameMessage(String type){
        this.type = type;
    }

}
