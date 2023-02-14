package com.knightlia.particle.sandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    protected <T> ResponseEntity<T> GET(String path, Class<T> res) {
        return restTemplate.getForEntity(url(path), res);
    }

    private String url(String path) {
        return "http://localhost:%d/%s".formatted(port, path);
    }
}
