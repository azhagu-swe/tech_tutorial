package com.azhag_swe.tech_tutorial.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.azhag_swe.tech_tutorial.security.service.UserDetailsImpl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${tech_tutorial.app.jwtSecret:}")
    private String jwtSecret;

    @Value("${tech_tutorial.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // Check if the provided secret is long enough for HS512 (>=512 bits, i.e., at least 64 bytes)
        if (jwtSecret == null || jwtSecret.trim().isEmpty() ||
            jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            // Generate a secure key using the library's secretKeyFor method
            key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            System.out.println("Generated secure key: " + key);
        } else {
            // Use the provided secret; Keys.hmacShaKeyFor will throw an exception if it's too short.
            key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                   .setSubject(userPrincipal.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }
    public String generateJwtTokenForUser(UserDetailsImpl userDetails) {
        return Jwts.builder()
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Log the exception if needed
        }
        return false;
    }
}
