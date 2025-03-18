package com.azhag_swe.tech_tutorial.model.repository;

import com.azhag_swe.tech_tutorial.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}