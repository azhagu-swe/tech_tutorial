package com.azhag_swe.tech_tutorial.repository;

import com.azhag_swe.tech_tutorial.model.entity.PasswordResetToken;
import com.azhag_swe.tech_tutorial.model.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
