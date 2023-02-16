package com.knightlia.particle.sandbox.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.knightlia.particle.sandbox.model.RequiresToken;
import com.knightlia.particle.sandbox.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketSession;

import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final Cache<WebSocketSession, String> sessionCache;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(RequiresToken.class)) {
                final String token = request.getHeader("token");
                if (token != null && sessionCache.asMap().containsValue(token)) {
                    return true;
                }

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(singletonList("error.token.invalid"))));
                return false;
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
