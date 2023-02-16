package com.knightlia.particle.sandbox.websocket;

import com.knightlia.particle.sandbox.AbstractTest;
import com.knightlia.particle.sandbox.TestWebSocketClient;
import com.knightlia.particle.sandbox.model.payload.MessageType;
import com.knightlia.particle.sandbox.model.request.NicknameRequest;
import jakarta.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static jakarta.websocket.ContainerProvider.getWebSocketContainer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

class WebSocketServiceTest extends AbstractTest {

    private static final int TIMEOUT = 4;
    private Session session;

    @BeforeEach
    void setup() {
        setupTest();
    }

    @AfterEach
    void teardown() throws Exception {
        session.close();
    }

    @Test
    void onConnectReturnsTokenAndUserListPayloadTest() throws Exception {
        final int count = 2;

        CountDownLatch latch = new CountDownLatch(count);
        TestWebSocketClient client = new TestWebSocketClient(latch);
        session = getWebSocketContainer().connectToServer(client, webSocketUrl());

        // Wait for initial messages
        if (!latch.await(TIMEOUT, TimeUnit.SECONDS)) {
            fail("CountDownLatch timed out.");
        }

        // Assert
        List<String> messageList = client.getMessageList();
        assertThat(messageList, hasSize(count));
        assertThat(messageList.get(0), containsString(MessageType.TOKEN_PAYLOAD.name()));
        assertThat(messageList.get(1), containsString(MessageType.USER_LIST_PAYLOAD.name()));
    }

    @Test
    void receivesUserListOnNicknameRequest() throws Exception {
        final String nickname = "websocket-nickname";

        CountDownLatch latch = new CountDownLatch(3);
        TestWebSocketClient client = new TestWebSocketClient(latch);
        session = getWebSocketContainer().connectToServer(client, webSocketUrl());

        // Send nickname request
        NicknameRequest request = new NicknameRequest();
        request.setNickname(nickname);
        ResponseEntity<List> response = POST("/nickname", headers(), request, List.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), is(2));

        // Wait for initial messages
        if (!latch.await(TIMEOUT, TimeUnit.SECONDS)) {
            fail("CountDownLatch timed out.");
        }

        // Assert
        List<String> messageList = client.getMessageList();
        assertThat(messageList, hasSize(3));

        final String message = messageList.get(2);
        assertThat(message, containsString(MessageType.USER_LIST_PAYLOAD.name()));
        assertThat(message, containsString(nickname));
    }
}
