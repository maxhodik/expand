package com.example.hodik.expand.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    protected void init() {
        jwtConfig.setSecretKey(Base64.getEncoder().encodeToString(jwtConfig.getSecretKey().getBytes()));
    }

    public String createToken(String username, boolean isRefreshToken) {
        Key key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
        Claims claims = Jwts.claims().setSubject(username);
        LocalDateTime now = LocalDateTime.now();
        Date validity;
        if (isRefreshToken) {
            LocalDateTime resultDate = now.plusDays(jwtConfig.getRefreshValidityInDays());
            validity = java.sql.Timestamp.valueOf(resultDate);
        } else {

            LocalDateTime resultDate = now.plusHours(jwtConfig.getValidityInHours());
            validity = java.sql.Timestamp.valueOf(resultDate);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(java.sql.Timestamp.valueOf(now))
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }


    public boolean validateToken(String token) {
        Jws<Claims> claimsJws = parseClaimsJws(token);
        return !claimsJws.getBody().getExpiration().before(new Date());
    }

    private Jws<Claims> parseClaimsJws(String token) {
        Key key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        Jws<Claims> claimsJws = parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return body.getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        if (authHeader != null && authHeader.contains(jwtConfig.getTokenPrefix())) {
            return authHeader.replace(jwtConfig.getTokenPrefix(), "");
        }
        //todo add log Inconsist authHeader
        return null;
    }
}
