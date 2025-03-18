package com.azhag_swe.tech_tutorial.security.service;

import com.azhag_swe.tech_tutorial.model.entity.User; // Ensure this is your custom User entity!
import com.azhag_swe.tech_tutorial.model.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch your custom User entity from the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Build a UserDetails instance from your custom User entity
        return UserDetailsImpl.build(user);
    }
}
