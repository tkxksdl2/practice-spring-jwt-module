package com.tkxksdk2.spring.settingjwtauth.websocket.stomp;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class StompController {

    @MessageMapping("/greeting")
    public String handle(String greeting) {
        return "[" + Instant.now() + ": " +  greeting;
    }
}
