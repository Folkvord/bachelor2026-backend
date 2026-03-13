package no.bachelor26.WebSocket;

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

import no.bachelor26.Service.AvailableTaskService;
import no.bachelor26.Service.TaskService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;


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
        session.getAttributes().put("userID", "332a4d65-2a84-423b-be83-53bc6d24f2e8");      // Alle er meg rn
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
                respondToTaskRequest(session, clientMessage.getData());
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
        
        UUID userID = (UUID) UUID.fromString((String) session.getAttributes().get("userID"));   // Midlertidig spaghetti
        ServerMessage reply = new ServerMessage();

        reply.setType("task-info");
        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                availableTaskService.getAvailableTaskInfo(userID)
            )
        );
        
        session.sendMessage(new TextMessage(
            objectMapper.writeValueAsString(
                reply
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
    private void respondToTaskRequest(WebSocketSession session, JsonNode data) throws Exception{

        UUID userID = UUID.fromString((String) session.getAttributes().get("userID"));   // Midlertidig spaghetti
        Long taskID = data.get("taskID").asLong();
        ServerMessage reply = new ServerMessage();

        reply.setType("task");

        // Har brukeren tilgang til oppgaven?
        if(availableTaskService.userHasAccessToTask(userID, taskID)){
            reply.setStatus("success");
            reply.setData(taskService.getTaskContentById(taskID));
        }
        else{
            reply.setStatus("error");
            reply.setData(prepareErrorMessage("no-access"));
        }
        
        session.sendMessage(new TextMessage(
            objectMapper.writeValueAsBytes(
                reply
            )
        ));

    }


    /**
     * Gjør klar en objectNode som inneholder en feilmelding
     * 
     * @param msg Feilmeldingen
     * @return ObjectNode med feilmeldingen
     */
    private ObjectNode prepareErrorMessage(String msg){
        return objectMapper.createObjectNode()
            .put("error", msg);
    }

}