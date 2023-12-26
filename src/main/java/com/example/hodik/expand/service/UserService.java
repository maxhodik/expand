package com.example.hodik.expand.service;

import com.example.hodik.expand.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByUsername(String username);

    void save(User user);
}
