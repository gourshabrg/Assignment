package com.Capstone.InterviewTracking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility component for creating, validating, and parsing JWT tokens.
 */
@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /** Secret key used to sign and verify JWT tokens. */
    @Value("${jwt.secret}")
    private String secret;

    /** Token validity period in milliseconds. */
    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    /**
     * Builds the signing key from the configured secret string.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT token for the given user.
     *
     * @param email the user's email address
     * @param role the user's role
     * @return the signed JWT token
     */
    public String generateToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        LOGGER.info("Generated JWT token for email: {}", email);
        return token;
    }

    /**
     * Extracts the email address from a JWT token.
     *
     * @param token the JWT token
     * @return the email address
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token
     * @return the role string
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Validates a JWT token by checking its signature and expiry.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            LOGGER.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Parses and returns the claims from a JWT token.
     *
     * @param token the JWT token
     * @return the parsed claims
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
