package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.constants.ErrorCodeConstants;
import com.azhag_swe.tech_tutorial.dto.request.PermissionRequest;
import com.azhag_swe.tech_tutorial.dto.response.ErrorResponse;
import com.azhag_swe.tech_tutorial.dto.response.PermissionResponse;
import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        List<PermissionResponse> responses = permissionService.getAllPermissions();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        try {
            PermissionResponse response = permissionService.getPermissionById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_NOT_FOUND, "Permission not found with id: " + id));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequest request) {
        try {
            PermissionResponse response = permissionService.createPermission(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_CREATE_ERROR,
                            "Error creating permission: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        try {
            PermissionResponse response = permissionService.updatePermission(id, request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_NOT_FOUND, "Permission not found with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_UPDATE_ERROR,
                            "Error updating permission: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_NOT_FOUND, "Permission not found with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.PERMISSION_DELETE_ERROR,
                            "Error deleting permission: " + ex.getMessage()));
        }
    }
}
