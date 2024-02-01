package com.tkxksdk2.spring.settingjwtauth.posts;


import com.tkxksdk2.spring.settingjwtauth.SocketCustomHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


record WsMessage (String message) {}
@RestController
public class PostResource {
    private SocketCustomHandler socketCustomHandler;

    public PostResource(SocketCustomHandler socketCustomHandler) {
        this.socketCustomHandler = socketCustomHandler;
    }

    @PostMapping("posts/send-message")
    public void sendMessageToAll(@RequestBody WsMessage wsMessage) {
        try {
            String message = wsMessage.message();
            socketCustomHandler.sendStringMessageToAll(message);
        } catch (Exception e) {}
    }
}
