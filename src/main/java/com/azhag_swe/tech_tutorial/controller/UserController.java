package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.constants.ErrorCodeConstants;
import com.azhag_swe.tech_tutorial.dto.request.CreateUserRequest;
import com.azhag_swe.tech_tutorial.dto.request.UpdateUserRequest;
import com.azhag_swe.tech_tutorial.dto.response.ErrorResponse;
import com.azhag_swe.tech_tutorial.dto.response.UserResponse;
import com.azhag_swe.tech_tutorial.exception.ResourceNotFoundException;
import com.azhag_swe.tech_tutorial.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse response = userService.createUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_CREATION_ERROR,
                            "Error creating user: " + ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserResponse response = userService.getUserById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_NOT_FOUND, "User not found with id: " + id));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_NOT_FOUND, "User not found with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_UPDATE_ERROR, "Error updating user: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_NOT_FOUND, "User not found with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(ErrorCodeConstants.USER_DELETE_ERROR, "Error deleting user: " + ex.getMessage()));
        }
    }
}
