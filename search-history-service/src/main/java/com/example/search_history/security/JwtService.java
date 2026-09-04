package com.example.search_history.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey cachedKey;
    private JwtParser cachedParser;

    @PostConstruct
    public void init() {
        this.cachedKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.cachedParser = Jwts.parser()
                .verifyWith(this.cachedKey)
                .build();
    }

    public Claims extractAllClaims(String token) {
        return cachedParser.parseSignedClaims(token).getPayload();
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
