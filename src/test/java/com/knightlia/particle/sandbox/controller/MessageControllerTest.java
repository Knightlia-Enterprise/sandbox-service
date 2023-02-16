package com.knightlia.particle.sandbox.controller;

import com.knightlia.particle.sandbox.AbstractTest;
import com.knightlia.particle.sandbox.model.request.MessageRequest;
import com.knightlia.particle.sandbox.model.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

class MessageControllerTest extends AbstractTest {

    @BeforeEach
    void setup() {
        setupTest();
    }

    @Test
    void sendMessageFails401OnInvalidTokenTest() {
        HttpHeaders headers = headers();
        headers.set("token", "invalid-token");

        ResponseEntity<ErrorResponse> response = POST("/message", headers, request(), ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.token.invalid"))));
    }

    @Test
    void sendMessageFails400WithMissingMessageTest() {
        MessageRequest request = request();
        request.setMessage(null);

        ResponseEntity<ErrorResponse> response = POST("/message", headers(), request, ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.message.required"))));
    }

    @Test
    void sendMessageFails400WithInvalidTimestampTest() {
        MessageRequest request = request();
        request.setTimestamp(0);

        ResponseEntity<ErrorResponse> response = POST("/message", headers(), request, ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.timestamp.invalid"))));
    }

    @Test
    void sendMessageFails400WithBadRequestTest() {
        ResponseEntity<ErrorResponse> response = POST("/message", headers(), new MessageRequest(), ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(asList(
            "error.message.required",
            "error.timestamp.invalid"
        ))));
    }

    @Test
    void sendMessageFails404IfUserNotFound() {
        ResponseEntity<ErrorResponse> response = POST("/message", headers(), request(), ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.user.not.found"))));
    }

    @Test
    void sendMessageSuccess200() {
        HttpHeaders headers = headers();
        headers.set("token", "token-3");

        ResponseEntity<?> response = POST("/message", headers, request(), void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    private MessageRequest request() {
        MessageRequest request = new MessageRequest();
        request.setMessage("This is a test message.");
        request.setTimestamp(new Date().getTime());
        return request;
    }
}
