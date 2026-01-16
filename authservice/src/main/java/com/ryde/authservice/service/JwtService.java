package com.ryde.authservice.service;

import com.ryde.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpire;

    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("ver", user.getTokenVersion());
        claims.put("roles", user.getRoles());
        claims.put("userId", user.getId());

        return Jwts
                .builder()
                .setClaims(claims)
                .subject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpire))
                .signWith(signinKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key signinKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(signinKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public int extractTokenVersion(String token) {
        return extractClaims(token).get("ver", Integer.class);
    }

    public Long extractUserId(String token){
        return extractClaims(token).get("userId", Long.class);
    }
}