package com.microservice.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    private Key key;
    private final long validity = 1000L * 60 * 60 * 24; // 24h

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateToken(String username, String role, Long userId) {
        Date now = new Date();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", username);
        data.put("role", role);
        data.put("userId", userId);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(data)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validity))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsername(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return body.getSubject();
    }
}
