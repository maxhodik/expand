package com.example.hodik.expand.service.impl;

import com.example.hodik.expand.model.User;
import com.example.hodik.expand.repository.UserRepository;
import com.example.hodik.expand.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
