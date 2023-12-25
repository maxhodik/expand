package com.example.hodik.expand.repository;

import com.example.hodik.expand.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
