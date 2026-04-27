package no.bachelor26.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.ObjectMapper;

@Service
public class WebSocketSender {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ObjectMapper objectMapper;

    private Map<UUID, WebSocketSession> activeSessions = new ConcurrentHashMap<>();


    
    /**
     * Legger til en ny sesjon.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     * @param session WebSocketSession-objektet handleren ga
     */
    public void appendSession(UUID userID, WebSocketSession session){
        if(activeSessions.containsKey(userID)){
            //throw new UserAlreadyHasWebSocketSessionException();  // fyfan trenger et nytt navn
        }
        activeSessions.put(userID, session);
    }



    /**
     * Fjerner en sesjon.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     */
    public void removeSession(UUID userID){
        if(activeSessions.containsKey(userID)){
            activeSessions.remove(userID);
        }
    }



    /**
     * Sender en melding til klienten.
     * 
     * @param userID ID-en på brukeren som får meldingen
     * @param msg GameMessage-meldingen som sendes
     * @author Kristoffer Folkvord
     */
    public void send(UUID userID, GameMessage msg){
        try{
            getSession(userID)
                .sendMessage(new TextMessage(
                    objectMapper.writeValueAsBytes(msg)
                )
            );
        } catch(IOException e){
            handleDisconnect(userID);
        }
    }



    /**
     * Sender en feilmelding til klienten.
     * 
     * @param userID ID-en på brukeren som får meldingen
     * @param errMsg GameMessage-meldingen som sendes
     * @param errDesc En beskrivelse av feilen
     * @author Kristoffer Folkvord
     */
    public void sendError(UUID userID, GameMessage errMsg, String errDesc){
        errMsg.setStatus("error");
        errMsg.setData(
            objectMapper.readTree("{\"desc\":\"" + errDesc + "\"}")
        );
        send(userID, errMsg);
    }



    private void handleDisconnect(UUID userID){
        log.error("Kunne ikke sende melding til: (" + userID + ")");    // SKAL RYDDE OPP
    }



    /**
     * Henter et sesjonsobjekt basert på ID-en til brukeren som eier det.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     * @return Sesjonsobjektet
     */
    private WebSocketSession getSession(UUID userID){
        if(!activeSessions.containsKey(userID)){
            // throw new WebSocketSessionNonExistantException();    // enda en banger
        }
        return activeSessions.get(userID);
    }

}
