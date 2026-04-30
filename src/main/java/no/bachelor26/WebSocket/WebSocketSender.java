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

import no.bachelor26.WebSocket.Exception.DupeUserSessionException;
import no.bachelor26.WebSocket.Exception.NoUserSessionException;
import no.bachelor26.WebSocket.Exception.UnexpectedSessionShutdownException;
import no.bachelor26.WebSocket.Game.GameMessage;
import tools.jackson.databind.ObjectMapper;

@Service
public class WebSocketSender {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired ObjectMapper objectMapper;

    private Map<UUID, WebSocketSession> activeSessions = new ConcurrentHashMap<>();


    
    /**
     * Legger til en ny sesjon.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     * @param session WebSocketSession-objektet handleren ga
     */
    public void appendSession(UUID userID, WebSocketSession session){
        if(activeSessions.containsKey(userID)){
            throw new DupeUserSessionException();
        }
        activeSessions.put(userID, session);
    }



    /**
     * Fjerner en sesjon.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     */
    public void removeSession(UUID userID){
        activeSessions.remove(userID);
    }



    /**
     * Sender en melding til klienten.
     * 
     * @param userID ID-en på brukeren som får meldingen
     * @param msg GameMessage-meldingen som sendes
     * @author Kristoffer Folkvord
     */
    public void send(UUID userID, GameMessage msg){
        
        WebSocketSession session;
        try{
            session = getSession(userID);
        } catch(NoUserSessionException e){  // Forbindelsen er borte!!!
            log.error("Brukerens (ID: " + userID + ") WebSocket-sesjonsobjekt fantes ikke i activeSession");
            return;
        } catch(UnexpectedSessionShutdownException e){  // Forbindelsen er dau
            log.error("Brukerens (ID: " + userID + ") WebSocket-sesjonsobjekt var allerede lukket");
            handleDisconnect(userID);
            return;
        }

        try{
            session.sendMessage(new TextMessage(
                objectMapper.writeValueAsBytes(msg)
            ));
        } catch(IOException e){     // Forbindelsen daua git
            log.error("Brukerens (ID: " + userID + ") kunne ikke sende meldingen (IOException)");
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



    /**
     * Henter et sesjonsobjekt basert på ID-en til brukeren som eier det.
     * 
     * @param userID ID-en på brukeren som eier sesjonen
     * @throws NoUserSessionExceptino Dersom brukersesjonen ikke finnes i hashmappet
     * @throws UnexpectedSessionShutdownException Dersom WS-sesjonen har lukker
     * @return Sesjonsobjektet
     */
    private WebSocketSession getSession(UUID userID) 
        throws NoUserSessionException, UnexpectedSessionShutdownException{
        if(!activeSessions.containsKey(userID)){
            throw new NoUserSessionException();
        }
        
        WebSocketSession session = activeSessions.get(userID);
        if(!session.isOpen()){
            throw new UnexpectedSessionShutdownException();
        }

        return session;
    }



    /**
     * Kjører når en forbindelsesfeil oppstår; lukker forbindeslsen ordentlig.
     * 
     * @param userID BrukerID-en
     */
    private void handleDisconnect(UUID userID){
        try{
            activeSessions.get(userID).close();;
        } catch(IOException ignore){
            // Ignore dat bih
        }
    }

}
