package com.knightlia.particle.sandbox.controller;

import com.knightlia.particle.sandbox.AbstractTest;
import com.knightlia.particle.sandbox.model.request.NicknameRequest;
import com.knightlia.particle.sandbox.model.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class NicknameControllerTest extends AbstractTest {

    @BeforeEach
    void setup() {
        setupTest();
    }

    @Test
    void setNicknameFails401WithInvalidTokenTest() {
        HttpHeaders headers = headers();
        headers.set("token", "invalid-token");

        ResponseEntity<ErrorResponse> response = POST("/nickname", headers, request(), ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.token.invalid"))));
    }

    @Test
    void setNicknameFails400WithMissingNicknameTest() {
        NicknameRequest request = request();
        request.setNickname(null);

        ResponseEntity<ErrorResponse> response = POST("/nickname", headers(), request, ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.nickname.required"))));
    }

    @Test
    void setNicknameFails400WithInvalidNicknameTest() {
        NicknameRequest request = request();
        request.setNickname("invalid nickname");

        ResponseEntity<ErrorResponse> response = POST("/nickname", headers(), request, ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.nickname.invalid"))));
    }

    @Test
    void setNicknameFails200WithExistingNicknameTest() {
        NicknameRequest request = request();
        request.setNickname("existing-nickname");

        ResponseEntity<ErrorResponse> response = POST("/nickname", headers(), request, ErrorResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), samePropertyValuesAs(new ErrorResponse(singletonList("error.nickname.exists"))));
    }

    @Test
    @DirtiesContext
    void setNicknameSuccess200ReturnsUserListTest() {
        ResponseEntity<List<String>> response = POST("/nickname", headers(), request(), new ParameterizedTypeReference<>() {
        });

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), containsInAnyOrder("nickname", "existing-nickname", "websocket-user"));
    }

    private NicknameRequest request() {
        NicknameRequest request = new NicknameRequest();
        request.setNickname("nickname");
        return request;
    }
}
