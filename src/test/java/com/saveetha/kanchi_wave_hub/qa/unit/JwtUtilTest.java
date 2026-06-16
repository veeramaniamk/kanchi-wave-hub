package com.saveetha.kanchi_wave_hub.qa.unit;

import com.saveetha.kanchi_wave_hub.component.JwtUtil;
import com.saveetha.kanchi_wave_hub.model.Users;
import com.saveetha.kanchi_wave_hub.qa.util.TestResultCollector;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Users user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        user = new Users();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setAddress("123 Saree St");
        user.setPhone(9876543210L);
        user.setUserType(100);
        // Explicit reflection check or setter for ID is not needed if we mock, but let's see. Users does not have a setter for ID, but we can set it via reflection if needed. Wait!
        // Users does not have a setUserId or setId setter in model. It has:
        // public int getId() { return id; }
        // Oh! No setter for id! Let's verify how UserService / JwtUtil handles user ID.
        // In JwtUtil:
        // claims.put("userId", user.getId());
        // Since ID is a primitive int, it defaults to 0, which is perfectly fine for JWT generation testing!
    }

    @Test
    void testGenerateAndDecodeTokenSuccess() {
        long start = System.currentTimeMillis();
        try {
            String token = jwtUtil.generateToken(user);
            assertNotNull(token);

            Claims claims = jwtUtil.decodeToken(token);
            assertEquals("test@example.com", claims.get("email"));
            assertNull(claims.getSubject());
            assertEquals("Kanchi Wave Hub", claims.getIssuer());

            assertEquals("test@example.com", jwtUtil.extractEmail(token));
            assertEquals(0, jwtUtil.extractUserId(token));

            TestResultCollector.addResult("testGenerateAndDecodeTokenSuccess", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Successfully generated and verified JWT tokens");
        } catch (Exception e) {
            TestResultCollector.addResult("testGenerateAndDecodeTokenSuccess", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "JWT unit test failed");
            fail(e);
        }
    }

    @Test
    void testExpiredTokenThrowsException() {
        long start = System.currentTimeMillis();
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(
                    "kanchi-wave-hub-very-secure-and-long-key-string-1234567890".getBytes(StandardCharsets.UTF_8)
            );
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", "expired@example.com");
            claims.put("userId", 1);
            String expiredToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 1 second ago
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();

            assertThrows(ExpiredJwtException.class, () -> jwtUtil.decodeToken(expiredToken));
            TestResultCollector.addResult("testExpiredTokenThrowsException", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Correctly threw ExpiredJwtException on expired tokens");
        } catch (Exception e) {
            TestResultCollector.addResult("testExpiredTokenThrowsException", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "JWT expired unit test failed");
            fail(e);
        }
    }

    @Test
    void testInvalidTokenThrowsException() {
        long start = System.currentTimeMillis();
        try {
            SecretKey wrongKey = Keys.hmacShaKeyFor(
                    "wrong-and-incorrect-key-string-1234567890-very-long-secret-key".getBytes(StandardCharsets.UTF_8)
            );
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", "invalid@example.com");
            claims.put("userId", 1);
            String invalidToken = Jwts.builder()
                    .setClaims(claims)
                    .signWith(wrongKey, SignatureAlgorithm.HS256)
                    .compact();

            assertThrows(SignatureException.class, () -> jwtUtil.decodeToken(invalidToken));
            TestResultCollector.addResult("testInvalidTokenThrowsException", "Unit", "PASSED", System.currentTimeMillis() - start, 0, null, 100, "Correctly threw SignatureException on altered signatures");
        } catch (Exception e) {
            TestResultCollector.addResult("testInvalidTokenThrowsException", "Unit", "FAILED", System.currentTimeMillis() - start, 0, e.getMessage(), 100, "JWT signature unit test failed");
            fail(e);
        }
    }
}
