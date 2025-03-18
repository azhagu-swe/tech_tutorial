package com.azhag_swe.tech_tutorial.dto.response;

import java.util.Set;

public class RoleResponse {

    private Long id;
    private String name;
    // For simplicity, a set of permission names
    private Set<String> permissions;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<String> getPermissions() {
        return permissions;
    }
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}

