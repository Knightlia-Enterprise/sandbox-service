package com.knightlia.particle.sandbox.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.knightlia.particle.sandbox.exception.NicknameExistsException;
import com.knightlia.particle.sandbox.model.payload.MessageType;
import com.knightlia.particle.sandbox.model.payload.UserListPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final Cache<String, String> userCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Collection<String> setNickname(String token, String nickname) {
        if (userCache.asMap().containsValue(nickname)) {
            throw new NicknameExistsException("error.nickname.exists");
        }

        userCache.put(token, nickname);

        Collection<String> userList = userCache.asMap().values();
        applicationEventPublisher.publishEvent(new UserListPayload(MessageType.USER_LIST_PAYLOAD, userList));
        return userList;
    }
}
