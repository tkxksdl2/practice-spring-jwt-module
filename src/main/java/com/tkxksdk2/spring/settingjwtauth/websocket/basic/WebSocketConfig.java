package com.tkxksdk2.spring.settingjwtauth.websocket.basic;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private SocketCustomHandler socketCustomHandler;

    public WebSocketConfig(SocketCustomHandler socketCustomHandler) {
        this.socketCustomHandler = socketCustomHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketCustomHandler, "/posts")
                .setAllowedOrigins("*");
              //.withSockJS();
    }

}
