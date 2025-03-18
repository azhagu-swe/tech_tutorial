package com.azhag_swe.tech_tutorial.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    // Optionally, a set of permission IDs can be passed
    private Set<Long> permissionIds;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
