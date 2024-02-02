package com.tkxksdk2.spring.settingjwtauth.posts;


import com.tkxksdk2.spring.settingjwtauth.websocket.basic.SocketCustomHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


record WsMessage (String message) {}
@RestController
public class PostResource {
    private SocketCustomHandler socketCustomHandler;

    private SimpMessagingTemplate messageSender;

    public PostResource(SocketCustomHandler socketCustomHandler,
                        SimpMessagingTemplate messageSender) {
        this.socketCustomHandler = socketCustomHandler;
        this.messageSender = messageSender;
    }

    @PostMapping("posts/send-message")
    public void sendMessageToAll(@RequestBody WsMessage wsMessage) {
        try {
            String message = wsMessage.message();
            socketCustomHandler.sendStringMessageToAll(message);
        } catch (Exception e) {}
    }

    @PostMapping("posts/send-message-stomp")
    public void sendMessageToAllByStomp(@RequestBody WsMessage wsMessage) {
        messageSender.convertAndSend("/topic/posts/new", wsMessage);
    }
}
