package com.saveetha.kanchi_wave_hub.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import com.saveetha.kanchi_wave_hub.model.Users;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "kanchi-wave-hub-very-secure-and-long-key-string-1234567890".getBytes(StandardCharsets.UTF_8)
    );
    public String generateToken(Users user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        return Jwts.builder().setSubject("Kanchi")
                .setClaims(claims)
                .setIssuer("Kanchi Wave Hub")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))) 
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)  // Set the signing key (must be the same as used for signing the token)
            .build()
            .parseClaimsJws(token)  // Parse the token
            .getBody();
    }

    public Integer extractUserId(String token) {
        Claims claims = decodeToken(token);
        return Integer.parseInt(claims.get("userId").toString());  // Extract userId
    }

    // Method to extract email from the token
    public String extractEmail(String token) {
        Claims claims = decodeToken(token);
        return claims.get("email", String.class);  // Extract email
    }
}

