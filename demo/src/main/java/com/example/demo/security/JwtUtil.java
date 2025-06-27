package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {
    @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
    private String secretKey;

    @org.springframework.beans.factory.annotation.Value("${jwt.expiration}")
    private long expirationMs;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", getUserRole(userDetails));
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .header().type("JWT").and()
            .claims().add(claims) 
            .and() 
            .subject(userDetails.getUsername()) 
            .issuedAt(now) 
            .expiration(expirationDate) 
            .signWith(getSigningKey()) 
            .compact(); 
    }
    
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            if (!username.equals(userDetails.getUsername())) {
                return false;
            }
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        Jws<Claims> jwsClaims = Jwts.parser() 
            .verifyWith(getSigningKey()) 
            .build() 
            .parseSignedClaims(token); 
        return jwsClaims.getPayload(); 
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); 
    }

    private String getUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse(null);
    }
}