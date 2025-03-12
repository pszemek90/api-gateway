package com.prezcode.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class CsrfMatcher implements ServerWebExchangeMatcher {

    private static final Logger log = LoggerFactory.getLogger(CsrfMatcher.class);

    private static final List<String> ALLOWED_PATHS = List.of("/login", "/actuator");

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        boolean hasBearerToken = authHeader != null && authHeader.startsWith("Bearer ");

        return (hasBearerToken || isAllowedPath(path))
                ? MatchResult.notMatch()
                : MatchResult.match();
    }

    private boolean isAllowedPath(String path) {
        log.info("Checking allowed paths with path: {}", path);
        return ALLOWED_PATHS.stream().anyMatch(path::startsWith);
    }
}
