package com.knightlia.particle.sandbox.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public Cache<WebSocketSession, String> sessionCache() {
        return Caffeine.newBuilder()
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build();
    }

    @Bean
    public Cache<String, String> userCache() {
        return Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build();
    }
}
