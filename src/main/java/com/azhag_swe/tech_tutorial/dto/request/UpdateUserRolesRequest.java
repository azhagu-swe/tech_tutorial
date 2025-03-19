package com.azhag_swe.tech_tutorial.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRolesRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Set<String> roles;
}
