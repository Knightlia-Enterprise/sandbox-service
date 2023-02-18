package com.knightlia.particle.sandbox.websocket;

import com.knightlia.particle.sandbox.AbstractTest;
import com.knightlia.particle.sandbox.TestWebSocketClient;
import com.knightlia.particle.sandbox.model.payload.MessageType;
import com.knightlia.particle.sandbox.model.request.MessageRequest;
import com.knightlia.particle.sandbox.model.request.NicknameRequest;
import jakarta.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
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

        // Wait for messages
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
    @DirtiesContext
    void receivesUserListOnNicknameRequestTest() throws Exception {
        final String nickname = "websocket-nickname";

        CountDownLatch latch = new CountDownLatch(3);
        TestWebSocketClient client = new TestWebSocketClient(latch);
        session = getWebSocketContainer().connectToServer(client, webSocketUrl());

        // Send nickname request
        NicknameRequest request = new NicknameRequest();
        request.setNickname(nickname);

        ResponseEntity<?> response = POST("/nickname", headers(), request, void.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Wait for messages
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

    @Test
    void receivesMessageOnWhenMessageSentTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        TestWebSocketClient client = new TestWebSocketClient(latch);
        session = getWebSocketContainer().connectToServer(client, webSocketUrl());

        // Send message
        HttpHeaders headers = headers();
        headers.set("token", "token-3");

        MessageRequest request = new MessageRequest();
        request.setMessage("This is a test message.");
        request.setTimestamp(new Date().getTime());

        ResponseEntity<?> response = POST("/message", headers, request, void.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Wait for messages
        if (!latch.await(TIMEOUT, TimeUnit.SECONDS)) {
            fail("CountDownLatch timed out.");
        }

        // Assert
        List<String> messageList = client.getMessageList();
        assertThat(messageList, hasSize(3));

        final String message = messageList.get(2);
        assertThat(message, containsString(MessageType.MESSAGE_PAYLOAD.name()));
        assertThat(message, containsString(request.getMessage()));
    }
}
