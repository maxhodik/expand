package com.example.hodik.expand.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.example.projectapp.auth.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
        try {
            tryAuthenticateByToken(token);
        } catch (ExpiredJwtException ex) {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String requestURL = httpRequest.getRequestURL().toString();
            if (requestURL.contains("refreshToken")) {
                String refreshToken = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
                try {
                    if (token == null && !jwtTokenProvider.validateToken(refreshToken)) {
                        throwJwtAuthExceptionWithMessage("JWT refresh_token is expired or invalid", servletResponse);
                    }
                    Authentication usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(null, null, null);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } catch (JwtException e) {
                    throwJwtAuthExceptionWithMessage("JWT refresh_token is expired or invalid", servletResponse);
                }
            } else {
                throwJwtAuthExceptionWithMessage("JWT token is expired or invalid and it's not a refresh request", servletResponse);
            }
        } catch (JwtException e) {
            throwJwtAuthExceptionWithMessage("JWT token is expired or invalid", servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void tryAuthenticateByToken(String token) {
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = authService.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void throwJwtAuthExceptionWithMessage(String s, ServletResponse servletResponse) throws IOException {
        SecurityContextHolder.clearContext();
        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        throw new JwtAuthenticationException(s);
    }
}
