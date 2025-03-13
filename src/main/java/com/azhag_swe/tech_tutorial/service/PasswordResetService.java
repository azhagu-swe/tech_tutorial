package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.model.entity.PasswordResetToken;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.PasswordResetTokenRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PasswordResetService {

    // Token validity: 1 hour (3600000 milliseconds)
    private static final long EXPIRATION_MILLIS = 3600000;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Creates a new password reset token for the user with the given email,
     * deletes any existing tokens, and sends a reset email.
     */
    @Transactional
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Delete existing tokens for the user, if any.
        tokenRepository.deleteByUser(user);

        // Generate a secure token. Here we use UUID for simplicity.
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(EXPIRATION_MILLIS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        tokenRepository.save(resetToken);

        // Send the reset email asynchronously.
        // sendPasswordResetEmail(user.getEmail(), token);
    }

    @Async
    public void sendPasswordResetEmail(String recipientEmail, String token) {
        // Build the password reset URL (adjust domain/port as needed)
        String resetUrl = "http://localhost:8082/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link:\n" + resetUrl);
        mailSender.send(message);
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token."));
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Password reset token has expired.");
        }
        return resetToken;
    }

    @Transactional
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}
