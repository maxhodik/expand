package com.example.hodik.expand.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenService {

    private final JwtConfig jwtConfig;

    public JwtTokenService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    protected void init() {
        jwtConfig.setSecretKey(Base64.getEncoder().encodeToString(jwtConfig.getSecretKey().getBytes()));
    }

    public String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        if (authHeader != null && authHeader.contains(jwtConfig.getTokenPrefix())) {
            return authHeader.replace(jwtConfig.getTokenPrefix(), "").trim();
        }
        //todo add log Inconsist authHeader
        return null;
    }

    public boolean validateToken(String token) {
        Jws<Claims> claimsJws = parseClaimsJws(token);
        return claimsJws.getBody().getExpiration().after(new Date());

    }

    public String getSubject(String token) {
        Jws<Claims> claimsJws = parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    public String getRole(String token) {
        Jws<Claims> claimsJws = parseClaimsJws(token);
        return claimsJws.getBody().get("role", String.class);
    }

    private Jws<Claims> parseClaimsJws(String token) {
        Key key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
