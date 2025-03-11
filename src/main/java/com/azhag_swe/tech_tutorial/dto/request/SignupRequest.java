package com.azhag_swe.tech_tutorial.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    private String username;
  
    @NotBlank
    @Email
    private String email;
  
    @NotBlank
    private String password;
  
    // Optional: list of roles (e.g., ["admin"])
    private Set<String> role;

    // Getters and setters
}

