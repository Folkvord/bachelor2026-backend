package no.bachelor26.WebSocket;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import no.bachelor26.Exception.NoTaskAccessException;
import no.bachelor26.Projection.TaskInfo;
import no.bachelor26.Service.AvailableTaskService;
import no.bachelor26.Service.TaskService;
import no.bachelor26.WebSocket.Messages.ClientMessages.ClientMessage;
import no.bachelor26.WebSocket.Messages.ClientMessages.RequestTaskMessage;
import tools.jackson.databind.ObjectMapper;


@Component
public class GameHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AvailableTaskService availableTaskService;

    @Autowired
    TaskService taskService;

    private final String ERRORMSG = "Unknown error";


    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("Klient tilkoblet");
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("Klient koblet fra");
    }
    

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> msg) throws Exception{

        log.info("Klient sendte medling");

        String payload = msg.getPayload().toString();
        ClientMessage clientMessage = objectMapper.readValue(payload, ClientMessage.class);

        switch(clientMessage.getType()){

            case "task-info":
                respondToTaskInfo(session);
                break;

            case "task":
                respondToTaskRequest(session, clientMessage);
                break;

            case "validate-flag":

                break;

            default:
                log.warn("wtf STYGG melding incoming");
                session.sendMessage(new TextMessage(ERRORMSG));
                break;

        }

    }


    /**
     * Sender klienten en liste med informasjonen over de tilgjengelige opppgavene.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void respondToTaskInfo(WebSocketSession session) throws Exception{
        
        UUID userID = (UUID) session.getAttributes().get("userID");
        List<TaskInfo> taskInfo = availableTaskService.getAvailableTaskInfo(userID);
        
        session.sendMessage(new TextMessage(
            objectMapper.writeValueAsBytes(
                taskInfo
            )
        ));

    }


    /**
     * Sender klienten oppgaven dersom brukeren har tilgang.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @param clientMessage Klientmeldingen
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void respondToTaskRequest(WebSocketSession session, ClientMessage clientMessage) throws Exception{

        UUID userID = (UUID) session.getAttributes().get("userID");
        Long taskID = ((RequestTaskMessage) clientMessage).getTaskID();
        
        if(!availableTaskService.userHasAccessToTask(userID, taskID)){
            throw new NoTaskAccessException(userID.toString(), taskID.toString());
        }

        session.sendMessage(new TextMessage(
            objectMapper.writeValueAsBytes(
                taskService.getTaskContentById(taskID)
            )
        ));

    }

}
