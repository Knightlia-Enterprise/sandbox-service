package com.knightlia.particle.sandbox.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MetricsConfiguration {

    @Value("${sandbox.environment}")
    private String environment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() throws UnknownHostException {
        final String hostname = InetAddress.getLocalHost().getHostName();

        return registry -> registry.config().commonTags(List.of(
            Tag.of("environment", environment),
            Tag.of("application_name", applicationName),
            Tag.of("hostname", hostname)
        ));
    }

    @Bean
    @Profile("!TESTING")
    public CacheMetricsRegistrarManager registerCacheMetrics(CacheMetricsRegistrar cacheMetricsRegistrar,
                                                             Cache<?, ?> sessionCache,
                                                             Cache<?, ?> userCache) {
        return new CacheMetricsRegistrarManager(cacheMetricsRegistrar, Map.of(
            "sessionCache", sessionCache,
            "userCache", userCache
        ));
    }

    private static class CacheMetricsRegistrarManager {
        public CacheMetricsRegistrarManager(CacheMetricsRegistrar cacheMetricsRegistrar, Map<String, Cache> caches) {
            caches.forEach((name, cache) ->
                cacheMetricsRegistrar.bindCacheToRegistry(new CaffeineCache(name, cache), Tag.of("name", name)));
        }
    }
}
