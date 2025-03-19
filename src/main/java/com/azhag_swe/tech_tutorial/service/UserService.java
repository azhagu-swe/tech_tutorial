package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.dto.request.CreateUserRequest;
import com.azhag_swe.tech_tutorial.dto.request.UpdateUserRequest;
import com.azhag_swe.tech_tutorial.dto.response.UserResponse;
import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.mapper.UserMapper;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.model.entity.Role;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.azhag_swe.tech_tutorial.model.repository.UserRepository;
import com.azhag_swe.tech_tutorial.model.repository.RoleRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

        private static final Logger logger = LoggerFactory.getLogger(UserService.class);

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public UserResponse createUser(CreateUserRequest request) {
                User user = UserMapper.toUser(request);
                // Encrypt the password
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                // If roles are provided, fetch and set them; otherwise, default role assignment
                // can be handled
                if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
                        Set<Role> roles = request.getRoleNames().stream()
                                        .map(roleName -> roleRepository.findByName(roleName)
                                                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name",
                                                                        roleName)))
                                        .collect(Collectors.toSet());
                        user.setRoles(roles);
                }
                User savedUser = userRepository.save(user);
                logger.info("User created with id: {}", savedUser.getId());
                return UserMapper.toUserResponse(savedUser);
        }

        public List<UserResponse> getAllUsers() {
                List<User> users = userRepository.findAll();
                return users.stream()
                                .map(UserMapper::toUserResponse)
                                .collect(Collectors.toList());
        }

        public UserResponse getUserById(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
                return UserMapper.toUserResponse(user);
        }

        @Transactional
        public UserResponse updateUser(Long id, UpdateUserRequest request) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
                UserMapper.updateUserFromRequest(user, request);
                if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                }
                if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
                        Set<Role> roles = request.getRoleNames().stream()
                                        .map(roleName -> roleRepository.findByName(roleName)
                                                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name",
                                                                        roleName)))
                                        .collect(Collectors.toSet());
                        user.setRoles(roles);
                }
                User updatedUser = userRepository.save(user);
                logger.info("User updated with id: {}", updatedUser.getId());
                return UserMapper.toUserResponse(updatedUser);
        }

        @Transactional
        public void deleteUser(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
                userRepository.delete(user);
                logger.info("User deleted with id: {}", id);
        }
}
