package com.tkxksdk2.spring.settingjwtauth;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * WebSocketHandler를 Service로 사용하여 다른 컨트롤러에서 참조할 수 있도록 함.
 * 따라서 다른 일반적인 http 요청을 통해 메세지가 전송될 수 있음.
 * <br/>
 * @method : sendStringMessageToAll(String message) 모든 WebSocketSession에 메세지 전송
 */
@Service
public class SocketCustomHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Principal principal = session.getPrincipal();
        String username = principal.getName();
        String payload = message.getPayload();
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage("User "+ username + " sent message: " + payload));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendStringMessageToAll(String message) throws Exception {
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(message));
        }
    }
}
