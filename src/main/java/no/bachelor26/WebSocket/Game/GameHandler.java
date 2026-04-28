package no.bachelor26.WebSocket.Game;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import no.bachelor26.Tasks.TaskSessions.TaskSessionService;
import no.bachelor26.User.UserSession.UserSession;
import no.bachelor26.WebSocket.WebSocketSender;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);

    @Autowired ObjectMapper objectMapper;
    @Autowired MessageRouter messageRouter;
    @Autowired WebSocketSender sender;
    @Autowired TaskSessionService taskSessionService;



    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("Klient tilkoblet");
        UUID userID = UUID.fromString("332a4d65-2a84-423b-be83-53bc6d24f2e8");      // Alle er Kristoffer rn
        UserSession userSession = new UserSession(userID);
        session.getAttributes().put("userSession", userSession);
        sender.appendSession(userID, session);
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("Klient koblet fra");
        
        UUID userID = ((UserSession) session.getAttributes()
            .get("userSession")).getUserID();

        sender.removeSession(userID);
        taskSessionService.cancelTaskSession(userID);
    }
    


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> msg) throws Exception{
        String payload = msg.getPayload().toString();
        GameMessage clientMessage = objectMapper.readValue(payload, GameMessage.class);
        UserSession userSession = (UserSession) session.getAttributes().get("userSession");

        // Pass på tullinger som sender meldinger for kjapt
        synchronized(userSession){
            messageRouter.routeGameMessage(userSession, clientMessage);
        }

    }



}