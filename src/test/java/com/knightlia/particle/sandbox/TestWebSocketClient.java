package com.knightlia.particle.sandbox;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
@RequiredArgsConstructor
@Slf4j
public class TestWebSocketClient {

    private final CountDownLatch latch;

    @Getter
    private final List<String> messageList = new ArrayList<>();

    @OnMessage
    public void onMessage(String message) {
        messageList.add(message);
        log.info("Incoming message: {}", message);
        latch.countDown();
    }
}
