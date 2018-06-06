package com.myprojects.grideye;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class Sender {

    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public Sender(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void temperaturePackage(String temperature) {
        messagingTemplate.convertAndSend("/topic/temperature", temperature);
    }
}
