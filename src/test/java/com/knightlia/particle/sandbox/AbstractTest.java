package com.knightlia.particle.sandbox;

import com.github.benmanes.caffeine.cache.Cache;
import io.sentry.spring.boot.jakarta.SentryAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SentryAutoConfiguration.class)
@ActiveProfiles("TESTING")
public abstract class AbstractTest {

    protected static final String VALID_TOKEN = "VALID_TOKEN";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Cache<WebSocketSession, String> sessionCache;

    @Autowired
    private Cache<String, String> userCache;

    protected void setupTest() {
        sessionCache.put(mock(WebSocketSession.class), VALID_TOKEN);
        sessionCache.put(mock(WebSocketSession.class), "token-3");
        userCache.put("token-2", "existing-nickname");
        userCache.put("token-3", "websocket-user");
    }

    protected <T> ResponseEntity<T> GET(String path, Class<T> res) {
        return restTemplate.getForEntity(url(path), res);
    }

    protected <T, K> ResponseEntity<K> POST(String path, HttpHeaders headers, T body, Class<K> res) {
        return restTemplate.exchange(requestEntity(path, headers, body), res);
    }

    protected <T, K> ResponseEntity<K> POST(String path, HttpHeaders headers, T body, ParameterizedTypeReference<K> res) {
        return restTemplate.exchange(requestEntity(path, headers, body), res);
    }

    protected HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", VALID_TOKEN);
        return headers;
    }

    protected URI webSocketUrl() {
        return URI.create("ws://localhost:%d/stream".formatted(port));
    }

    private String url(String path) {
        return "http://localhost:%d/%s".formatted(port, path);
    }

    private <T> RequestEntity<T> requestEntity(String path, HttpHeaders headers, T body) {
        return RequestEntity.post(url(path)).
            headers(headers).
            body(body);
    }
}
