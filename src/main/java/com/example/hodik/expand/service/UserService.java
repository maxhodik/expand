package com.example.hodik.expand.service;

import com.example.hodik.expand.auth.exception.UserAlreadyExistsException;
import com.example.hodik.expand.controller.dto.AuthenticationRequestDTO;
import com.example.hodik.expand.model.User;
import com.example.hodik.expand.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService {
    private final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registration(AuthenticationRequestDTO request) {
        String userName = request.getUsername();
        User user = userRepository.findUserByUserName(userName).get();
        if (user != null) {
            throw new UserAlreadyExistsException("User" + userName + " already exists");
        }
        String password = (request.getPassword());

    }
}
