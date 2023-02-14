package com.knightlia.particle.sandbox.controller;

import com.knightlia.particle.sandbox.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HealthControllerTest extends AbstractTest {

    @Test
    void getVersionTest() {
        ResponseEntity<String> response = GET("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
