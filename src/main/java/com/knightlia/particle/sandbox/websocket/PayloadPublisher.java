package com.knightlia.particle.sandbox.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayloadPublisher {

    private final ObjectMapper objectMapper;

    public <T> void publishSingleMessage(WebSocketSession session, T payload) {
        final TextMessage message = createTextMessage(payload);
        if (message != null) {
            publishSingleMessage(session, message);
        }
    }

    public <T> void broadcast(Set<WebSocketSession> sessionList, T payload) {
        TextMessage message = createTextMessage(payload);
        if (message != null) {
            sessionList.stream()
                .filter(WebSocketSession::isOpen)
                .forEach(session -> publishSingleMessage(session, message));
        }
    }

    private void publishSingleMessage(WebSocketSession session, TextMessage payload) {
        try {
            session.sendMessage(payload);
        } catch (IOException e) {
            log.error("Failed to publish websocket payload: {}", e.getMessage(), e);
        }
    }

    private <T> TextMessage createTextMessage(T payload) {
        try {
            return new TextMessage(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialise websocket payload: {}", e.getMessage(), e);
            return null;
        }
    }
}
