package no.bachelor26.WebSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import no.bachelor26.WebSocket.Messages.ClientMessages.ClientMessage;
import tools.jackson.databind.ObjectMapper;


@Component
public class GameHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);

    @Autowired
    ObjectMapper objectMapper;


    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("Klient tilkoblet");
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("Klient koblet fra");
    }
    

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> msg){

        log.info("Klient sendte medling");

        // msg burde inneholde en JSON som forklarer hva brukeren vil ha
        //String payload = msg.getPayload().toString();
        
        String payload = msg.getPayload().toString();
        ClientMessage clientMessage = objectMapper.readValue(payload, ClientMessage.class);

        switch(clientMessage.getType()){

            case "task-info":
                
                break;

            case "task":

                break;

            case "validate-flag":

                break;

            default:
                log.warn("wtf STYGG melding incoming");
                break;

        }

    }

}
