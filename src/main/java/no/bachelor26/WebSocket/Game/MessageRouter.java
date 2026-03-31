package no.bachelor26.WebSocket.Game;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Service.TaskService;
import no.bachelor26.WebSocket.WebSocketSender;

@Service
public class MessageRouter {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskService taskService;

    @Autowired
    WebSocketSender sender;
    


    public void routeGameMessage(UUID userID, GameMessage clientMessage){

        // Meldinger som ikke er avhengig av en oppgavesesjon
        switch(clientMessage.getType()){
            case "task-info":
                taskService.respondToTaskInfo(userID);
                break;

            case "task":
                taskService.respondToTask(userID, clientMessage.getData());
                break;

            case "parse-status":
                taskService.respondToParseStatus(userID, clientMessage);
                break;

            case "cancel-task":
                taskService.respondToCancelTask(userID);
                break;

            case "validate-flag":
                taskService.respondToValidateFlag(userID, clientMessage);
                break;

            default:
                handleInvalidMessageType(userID, clientMessage);
                break;
        }

    }

    

    private void handleInvalidMessageType(UUID userID, GameMessage clientMessage){
        log.error("Klient: (" + userID + "), sendte en melding med en ugyldig type: " + clientMessage.getType());

    }

}
