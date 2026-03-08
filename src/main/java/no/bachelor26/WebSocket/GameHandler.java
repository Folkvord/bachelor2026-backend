package no.bachelor26.WebSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameHandler extends TextWebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession session){

        System.out.println("TILKOBLET!!!");

    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){

        System.out.println("AVKOBLET!!!");

    }
    

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> msg){

        System.out.println();

    }

}
