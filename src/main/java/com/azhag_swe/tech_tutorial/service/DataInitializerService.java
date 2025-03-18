package com.azhag_swe.tech_tutorial.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.model.repository.RoleRepository;
import com.azhag_swe.tech_tutorial.model.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class DataInitializerService {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initializeData() {
        logger.info("Starting data initialization...");
        initializeRoles();
        createTechAdminUser();
        logger.info("Data initialization completed.");
    }

    private void initializeRoles() {
        String[] predefinedRoles = { "Admin", "User", "Tech_admin", "Staff", "Manager" };
        for (String roleName : predefinedRoles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                logger.info("Role '{}' inserted into the database.", roleName);
            } else {
                logger.debug("Role '{}' already exists in the database. Skipping insertion.", roleName);
            }
        }
    }

    private void createTechAdminUser() {
        String defaultUsername = "tech_admin";
        String defaultEmail = "tech@gmail.com";
        String defaultPassword = "TechAdmin@123";

        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            User techAdmin = new User();
            techAdmin.setUsername(defaultUsername);
            techAdmin.setEmail(defaultEmail);
            techAdmin.setPassword(passwordEncoder.encode(defaultPassword));
            // Throw a custom exception if the role is missing instead of RuntimeException.
            Role techAdminRole = roleRepository.findByName("Tech_admin")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "Tech_admin"));
            techAdmin.setRoles(Set.of(techAdminRole));
            userRepository.save(techAdmin);
            logger.info("Default user '{}' created with role 'Tech_admin'.", defaultUsername);
        } else {
            logger.warn("Default user '{}' already exists. Skipping creation.", defaultUsername);
        }
    }
}
