package com.knightlia.particle.sandbox.websocket;

import com.github.benmanes.caffeine.cache.Cache;
import com.knightlia.particle.sandbox.model.payload.MessageType;
import com.knightlia.particle.sandbox.model.payload.TokenPayload;
import com.knightlia.particle.sandbox.model.payload.UserListPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService extends TextWebSocketHandler {

    private final PayloadPublisher payloadPublisher;
    private final Cache<WebSocketSession, String> sessionCache;
    private final Cache<String, String> userCache;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        final String token = session.getId();
        log.debug("New websocket client: {}", token);
        payloadPublisher.publishSingleMessage(session, new TokenPayload(MessageType.TOKEN_PAYLOAD, token));
        sessionCache.put(session, token);
        broadcastUserList(new UserListPayload(MessageType.USER_LIST_PAYLOAD, userCache.asMap().values()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("WebSocket connection closed: id={}, code={}", session.getId(), status.getCode());
        userCache.invalidate(session.getId());
        sessionCache.invalidate(session);
        broadcastUserList(new UserListPayload(MessageType.USER_LIST_PAYLOAD, userCache.asMap().values()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable e) {
        log.error("Error in websocket connection: id={}, message={}", session.getId(), e.getMessage(), e);
    }

    @EventListener
    public void broadcastUserList(UserListPayload payload) {
        log.info("Broadcasting user list: size={}", payload.userList().size());
        payloadPublisher.broadcast(sessionCache.asMap().keySet(), payload);
    }
}
