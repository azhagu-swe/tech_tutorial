package com.azhag_swe.tech_tutorial.repository;

import com.azhag_swe.tech_tutorial.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
