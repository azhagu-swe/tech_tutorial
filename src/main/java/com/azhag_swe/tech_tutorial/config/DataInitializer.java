package com.azhag_swe.tech_tutorial.config;

import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.RoleRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() {
        logger.info("Starting data initialization...");

        // Initialize predefined roles
        initializeRoles();

        // Create default tech_admin user
        createTechAdminUser();

        logger.info("Data initialization completed.");
    }

    private void initializeRoles() {
        String[] predefinedRoles = {"Admin", "User", "Tech_admin", "Staff", "Manager"};
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
        String defaultEmail="tech@gmail.com";
        String defaultPassword = "TechAdmin@123";

        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            User techAdmin = new User();
            techAdmin.setUsername(defaultUsername);
            techAdmin.setEmail(defaultEmail);
            techAdmin.setPassword(passwordEncoder.encode(defaultPassword));
            techAdmin.setRoles(Set.of(roleRepository.findByName("Tech_admin").orElseThrow(() -> 
                new RuntimeException("Tech_admin role not found in the database"))));
            userRepository.save(techAdmin);
            logger.info("Default user '{}' created with role 'Tech_admin'.", defaultUsername);
        } else {
            logger.warn("Default user '{}' already exists. Skipping creation.", defaultUsername);
        }
    }
}
