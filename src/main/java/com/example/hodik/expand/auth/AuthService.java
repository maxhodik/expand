package com.example.hodik.expand.auth;


import com.example.hodik.expand.model.User;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication getAuthentication(String token);

    User getUserFromAuth();

    User getUserFromAuth(Authentication authentication);

    String getUserEmail();

}
