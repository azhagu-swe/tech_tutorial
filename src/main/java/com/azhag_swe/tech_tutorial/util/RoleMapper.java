package com.azhag_swe.tech_tutorial.util;

import com.azhag_swe.tech_tutorial.dto.request.RoleRequest;
import com.azhag_swe.tech_tutorial.dto.response.RoleResponse;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.Permission;

import java.util.stream.Collectors;

public class RoleMapper {

    public static RoleResponse mapToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        if (role.getPermissions() != null) {
            response.setPermissions(
                    role.getPermissions()
                            .stream()
                            .map(Permission::getName)
                            .collect(Collectors.toSet()));
        }
        return response;
    }

    public static Role mapToEntity(RoleRequest roleRequest) {
        Role role = new Role();
        role.setName(roleRequest.getName());
        // Note: Mapping permissions (from IDs) should be handled in the service layer.
        return role;
    }
}
