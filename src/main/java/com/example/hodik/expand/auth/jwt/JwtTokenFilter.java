package com.example.hodik.expand.auth.jwt;

import com.example.hodik.expand.auth.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenService jwtTokenService;
    private final AuthService authService;


    public JwtTokenFilter(JwtTokenService jwtTokenService, AuthService authService) {
        this.jwtTokenService = jwtTokenService;
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenService.resolveToken((HttpServletRequest) servletRequest);
        try {
            tryAuthenticateByToken(token);
        } catch (ExpiredJwtException ex) {
            throwJwtAuthExceptionWithMessage("JWT token is expired or invalid ", servletResponse, ex);
        } catch (JwtException e) {
            throwJwtAuthExceptionWithMessage("JWT token is expired or invalid", servletResponse, e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    private void tryAuthenticateByToken(String token) {
        if (token != null && jwtTokenService.validateToken(token)) {
            Authentication authentication = authService.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void throwJwtAuthExceptionWithMessage(String s, ServletResponse servletResponse, Exception ex) throws IOException {
        SecurityContextHolder.clearContext();
        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        throw new JwtAuthenticationException(s, ex);
    }

}