package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.constants.AppConstants;
import com.azhag_swe.tech_tutorial.constants.ErrorCodeConstants;
import com.azhag_swe.tech_tutorial.dto.request.*;
import com.azhag_swe.tech_tutorial.dto.response.*;
import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.model.entity.PasswordResetToken;
import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.model.repository.RoleRepository;
import com.azhag_swe.tech_tutorial.model.repository.UserRepository;
import com.azhag_swe.tech_tutorial.security.service.RefreshTokenService;
import com.azhag_swe.tech_tutorial.security.service.UserDetailsImpl;
import com.azhag_swe.tech_tutorial.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(ErrorCodeConstants.USERNAME_TAKEN, "Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(ErrorCodeConstants.EMAIL_IN_USE, "Email is already in use!"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(AppConstants.DEFAULT_ROLE)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", AppConstants.DEFAULT_ROLE));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equalsIgnoreCase(role)) {
                    Role adminRole = roleRepository.findByName("Admin")
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(AppConstants.DEFAULT_ROLE)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
                    roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> refreshToken(@Valid TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.getByToken(requestRefreshToken);
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            try {
                refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            } catch (RuntimeException ex) {
                return ResponseEntity.badRequest()
                        .body(new TokenRefreshResponse("", "Refresh token expired. Please sign in again."));
            }
            Long userId = refreshToken.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for id: " + userId));
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);
            String newAccessToken = jwtUtils.generateJwtTokenForUser(userDetails);
            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
        } else {
            return ResponseEntity.badRequest()
                    .body(new TokenRefreshResponse("", "Refresh token is not in database!"));
        }
    }

    public ResponseEntity<?> forgotPassword(@Valid ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        PasswordResetToken resetToken = passwordResetService.createPasswordResetTokenForUser(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
        return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email"));
    }

    public ResponseEntity<?> resetPassword(@Valid ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetService.validatePasswordResetToken(request.getToken());
        User user = token.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        passwordResetService.deleteToken(token);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }
}
