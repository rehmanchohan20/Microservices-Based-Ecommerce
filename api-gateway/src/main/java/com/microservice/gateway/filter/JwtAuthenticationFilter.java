package com.microservice.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${JWT_SECRET_KEY}") // environment variable se inject
    private String secretKey;

    private Key key; // initialized after injection

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register"
    );

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("[JWT Filter] Incoming request path: " + path);

        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            System.out.println("[JWT Filter] Skipping JWT filter for: " + path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println("[JWT Filter] Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] Missing or invalid token. Returning 401.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        System.out.println("[JWT Filter] Token received: " + token);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("[JWT Filter] JWT subject: " + claims.getSubject());
            System.out.println("[JWT Filter] JWT expiration: " + claims.getExpiration());

            exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .build();

            System.out.println("[JWT Filter] token with new header: " + exchange);

        } catch (Exception e) {
            System.out.println("[JWT Filter] Token validation failed: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> System.out.println("[JWT Filter] Request to " + path + " successfully forwarded"))
                .doOnError(err -> System.out.println("[JWT Filter] Error while forwarding request: " + err.getMessage()));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
