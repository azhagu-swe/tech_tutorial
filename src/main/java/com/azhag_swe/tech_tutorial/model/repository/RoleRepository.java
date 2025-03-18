package com.azhag_swe.tech_tutorial.model.repository;

import com.azhag_swe.tech_tutorial.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    
}
