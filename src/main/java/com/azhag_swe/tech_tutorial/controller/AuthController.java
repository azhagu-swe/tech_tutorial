package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.dto.request.ForgotPasswordRequest;
import com.azhag_swe.tech_tutorial.dto.request.LoginRequest;
import com.azhag_swe.tech_tutorial.dto.request.ResetPasswordRequest;
import com.azhag_swe.tech_tutorial.dto.request.SignupRequest;
import com.azhag_swe.tech_tutorial.dto.request.TokenRefreshRequest;
import com.azhag_swe.tech_tutorial.dto.response.ErrorResponse;
import com.azhag_swe.tech_tutorial.dto.response.JwtResponse;
import com.azhag_swe.tech_tutorial.dto.response.MessageResponse;
import com.azhag_swe.tech_tutorial.dto.response.TokenRefreshResponse;
import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.model.entity.PasswordResetToken;
import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.RoleRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;
import com.azhag_swe.tech_tutorial.security.service.RefreshTokenService;
import com.azhag_swe.tech_tutorial.security.service.UserDetailsImpl;
import com.azhag_swe.tech_tutorial.service.EmailService;
import com.azhag_swe.tech_tutorial.service.PasswordResetService;
import com.azhag_swe.tech_tutorial.util.JwtUtils;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;

    // Add these new dependencies
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Generate a refresh token for this user
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken.getToken(), // if you choose to include it in JwtResponse
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("AUTH004", "Username is already taken!"));
        }

        // Check if email exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("AUTH005", "Email is already in use!"));
        }

        // Create new user account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Use default role "ROLE_USER"
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));
            roles.add(userRole);
        } else {
            // Iterate over provided roles and add corresponding Role entities
            strRoles.forEach(role -> {
                if ("admin".equalsIgnoreCase(role)) {
                    Role adminRole = roleRepository.findByName("Admin")
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        // 1. Extract the refresh token from the request payload.
        String requestRefreshToken = request.getRefreshToken();

        // 2. Try to find the refresh token in the database.
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.getByToken(requestRefreshToken);

        // 3. If the refresh token exists, proceed with token refresh.
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();

            // 4. Verify that the refresh token has not expired.
            try {
                refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            } catch (RuntimeException ex) {
                // If the token is expired, return an error response.
                return ResponseEntity.badRequest()
                        .body(new TokenRefreshResponse("", "Refresh token expired. Please sign in again."));
            }

            // 5. Get the user ID associated with the refresh token.
            Long userId = refreshToken.getUserId();

            // 6. Load the user from the database using the user ID.
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for id: " + userId));

            // 7. Convert the user entity into a UserDetails object.
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);

            // 8. Generate a new access token using the user's details.
            String newAccessToken = jwtUtils.generateJwtTokenForUser(userDetails);

            // 9. Return the new access token along with the same refresh token in the
            // response.
            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
        } else {
            // 10. If the refresh token is not found in the database, return an error
            // response.
            return ResponseEntity.badRequest()
                    .body(new TokenRefreshResponse("", "Refresh token is not in database!"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        PasswordResetToken resetToken = passwordResetService.createPasswordResetTokenForUser(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());

        return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetService.validatePasswordResetToken(request.getToken());

        User user = token.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetService.deleteToken(token);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

}
