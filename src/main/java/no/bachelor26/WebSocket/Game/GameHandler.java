package no.bachelor26.WebSocket.Game;

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

import no.bachelor26.Exception.TaskNotFoundException;
import no.bachelor26.Service.AvailableTaskService;
import no.bachelor26.Service.TaskService;
import tools.jackson.databind.JsonNode;
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
        GameMessage clientMessage = objectMapper.readValue(payload, GameMessage.class);

        switch(clientMessage.getType()){

            case "task-info":
                respondToTaskInfo(session);
                break;

            case "task":
                respondToTask(session, clientMessage.getData());
                break;

            case "validate-flag":

                break;

            default:
                log.warn("wtf STYGG melding incoming");
                sendError(session, new GameMessage(clientMessage.getType()), "invalid type");
                break;

        }

    }


    /**
     * API-et for henting av oppgaveinformasjon.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void respondToTaskInfo(WebSocketSession session) throws Exception{
        GameMessage reply = new GameMessage("task-info");
        
        UUID userID = (UUID) UUID.fromString((String) session.getAttributes().get("userID"));   // Midlertidig spaghetti
        if(userID == null){
            sendError(session, reply, "no userID");
            return;
        }
        
        reply.setStatus("success");
        reply.setData(
            objectMapper.valueToTree(
                availableTaskService.getAvailableTaskInfo(userID)
            )
        );
        
        send(session, reply);

    }


    /**
     * API-et for å hente innholdet til en oppgave.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @param clientMessage Klientmeldingen
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void respondToTask(WebSocketSession session, JsonNode data) throws Exception{
        GameMessage reply = new GameMessage("task");

        // Sjekk om noe fandango har skjedd med brukerID-en
        UUID userID = UUID.fromString((String) session.getAttributes().get("userID"));   // Midlertidig spaghetti
        if(userID == null){
            sendError(session, reply, "no userID");
            return;
        }

        // Er innholdet ikke en long?
        JsonNode content = data.get("taskID");
        if(!content.canConvertToLong()){
            sendError(session, reply, "invalid content");
            return;
        }

        Long taskID = content.asLong();

        // Har brukeren tilgang til oppgaven?
        if(!availableTaskService.userHasAccessToTask(userID, taskID)){
            sendError(session, reply, "no access");
            return;
        }
        
        // Oppgaven finnes ikke sant?
        try{
            JsonNode taskContent = taskService.getTaskContentById(taskID);
            reply.setData(taskContent);
        } catch(TaskNotFoundException e){
            sendError(session, reply, "no access");
            return;
        }

        reply.setStatus("success");
        send(session, reply);
        
    }


    /**
     * Sender en melding til klienten.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @param msg GameMessage-meldingen som sendes
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void send(WebSocketSession session, GameMessage msg) throws Exception{
        session.sendMessage(new TextMessage(
            objectMapper.writeValueAsBytes(
                msg
            )
        ));
    }


    /**
     * Sender en feilmelding til klienten.
     * 
     * @param session WebSocket-sesjonsobjekt
     * @param errMsg GameMessage-meldingen som sendes
     * @param errDesc En beskrivelse av feilen
     * @throws Exception
     * @author Kristoffer Folkvord
     */
    private void sendError(WebSocketSession session, GameMessage errMsg, String errDesc) throws Exception{
        errMsg.setStatus("error");
        errMsg.setData(
            objectMapper.readTree("{\"desc\":\"" + errDesc + "\"}")
        );
        send(session, errMsg);
    }


}