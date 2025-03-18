package com.azhag_swe.tech_tutorial.service;


import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Page<Role> getAllRoles(Pageable pageable) {
        logger.info("Fetching all roles with pagination");
        return roleRepository.findAll(pageable);
    }

    public Role getRoleById(Long id) {
        logger.info("Fetching role with id {}", id);
        return roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Transactional
    public Role createRole(Role role) {
        logger.info("Creating new role: {}", role.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        logger.info("Updating role with id {}", id);
        Role existingRole = getRoleById(id);
        existingRole.setName(roleDetails.getName());
        existingRole.setPermissions(roleDetails.getPermissions());
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        logger.info("Deleting role with id {}", id);
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }
}

