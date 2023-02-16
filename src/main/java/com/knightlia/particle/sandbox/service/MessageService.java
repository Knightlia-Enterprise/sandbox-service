package com.knightlia.particle.sandbox.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.knightlia.particle.sandbox.exception.UserNotFoundException;
import com.knightlia.particle.sandbox.model.payload.MessagePayload;
import com.knightlia.particle.sandbox.model.payload.MessageType;
import com.knightlia.particle.sandbox.model.request.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final Cache<String, String> userCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendMessage(String token, MessageRequest messageRequest) {
        // Get sender from token
        final String nickname = userCache.get(token, k -> {
            throw new UserNotFoundException("error.user.not.found");
        });

        // Broadcast
        MessagePayload messagePayload = new MessagePayload(
            MessageType.MESSAGE_PAYLOAD,
            nickname,
            messageRequest.getMessage(),
            messageRequest.getTimestamp()
        );

        applicationEventPublisher.publishEvent(messagePayload);
    }
}
