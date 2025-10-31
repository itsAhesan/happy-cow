package com.xworkz.happycow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPushService {
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public void sendToUser(String username, Object payload) {
        // username must match Principal.getName() from the WebSocket handshake
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", payload);
    }
}
