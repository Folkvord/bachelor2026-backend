package no.bachelor26.WebSocket.Game;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class GameMessage {

    private String type;
    private int requestID;
    private String status;
    private JsonNode data;

    public GameMessage(){}

    
    
    /**
     * Default konstruktør
     * 
     * @param type Typen
     */
    public GameMessage(String type){
        this.type = type;
    }



    /**
     * Konstruktør som lager et svar basert på en mottatt melding.
     * Den lager et nytt GameMessage-objekt med samme {@code type} og {@code requestID}
     * som finnes i parameterobjektet.
     * 
     * @param replyRecipient Meldingen som skal svares
     */
    public GameMessage(GameMessage replyRecipient){
        this.type = replyRecipient.getType();
        this.requestID = replyRecipient.getRequestID();
    }
    

}
