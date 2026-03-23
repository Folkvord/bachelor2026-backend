package no.bachelor26.WebSocket.Game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Service.TaskService;
import no.bachelor26.WebSocket.UserSession;
import no.bachelor26.WebSocket.WebSocketSender;

@Service
public class MessageRouter {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskService taskService;

    @Autowired
    WebSocketSender sender;
    



    /**
     * Ruter meldingen til tilstandsruteren pekt på av brukertilstanden.
     * 
     * @param userSession Bruker-ID og tilstand.
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    public void routeGameMessage(UserSession userSession, GameMessage msg){

        switch(userSession.getState()){
            case IDLE:
                routeIdleStateMessage(userSession, msg);
                break;

            case PARSE_STANDBY:
                routeParseStandbyStateMessage(userSession, msg);
                break;

            case ACTIVE_TASK:
                routeActiveTaskStateMessage(userSession, msg);
                break;

            default:
                handleInvalidMessageType(userSession, msg);
                break;
        }

    }



    /**
     * Ruter meldingen til en bruker i IDLE-tilstanden.
     * 
     * @param userSession Bruker-ID og tilstand.
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    private void routeIdleStateMessage(UserSession userSession, GameMessage msg){

        switch(msg.getType()){
            case "task-info":
                taskService.respondToTaskInfo(userSession);
                break;

            case "task":
                taskService.respondToTask(userSession, msg.getData());
                break;

            default:
                handleInvalidMessageType(userSession, msg);
                break;
        }

    }



    /**
     * Ruter meldingen til en bruker i PARSE_STANDBY-tilstanden.
     * 
     * @param userSession Bruker-ID og tilstand.
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    private void routeParseStandbyStateMessage(UserSession userSession, GameMessage msg){

        switch(msg.getType()){
            case "parse-status":
                taskService.respondToParseStatus(userSession, msg);
                break;
        
            default:
                handleInvalidMessageType(userSession, msg);
                break;
        }

    }



    /**
     * Ruter meldingen til en bruker i ACTIVE_TASK-tilstanden.
     * 
     * @param userSession Bruker-ID og tilstand.
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    private void routeActiveTaskStateMessage(UserSession userSession, GameMessage msg){

        switch(msg.getType()){
            case "validate-flag":
                taskService.respondToValidateFlag(userSession, msg);
                break;
            
            case "cancel-task":
                taskService.respondToCancelTask(userSession);
                break;
        
            default:
                handleInvalidMessageType(userSession, msg);
                break;
        }

    }



    /**
     * idk
     * 
     * @param userSession Bruker-ID og tilstand.
     * @param msg Meldingen
     * @author Kristoffer Folkvord
     */
    private void handleInvalidMessageType(UserSession userSession, GameMessage msg){
        log.error(
            "Klient: (" + userSession.getUserID() + "). Ugyldig tilstand for meldingstype: " + userSession.getState().name() + " -> " + msg.getType()
        );

        sender.sendError(
            userSession.getUserID(),
            new GameMessage(msg.getType()),
            "invalid state. Current state: " + userSession.getState().name()
        );

    }

}
