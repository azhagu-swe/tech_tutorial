package com.azhag_swe.tech_tutorial.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRequest {
    // Optional fields (only non-null fields will be updated)
    
    private String username;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Set<String> roleNames;
}
