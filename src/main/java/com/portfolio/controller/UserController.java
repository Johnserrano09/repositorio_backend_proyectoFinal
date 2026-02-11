package com.portfolio.controller;

import com.portfolio.dto.UserRequest;
import com.portfolio.dto.UserResponse;
import com.portfolio.model.Role;
import com.portfolio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User endpoints")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all active users with a specific role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userService.findByRole(roleEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Get list of all active users (requires auth)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllActive());
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user", description = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(UUID.fromString(id)));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Get user by email address")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmailResponse(email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(UUID.fromString(id), request));
    }

    @PutMapping("/email/{email}")
    @Operation(summary = "Update user by email", description = "Update user by email address")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> updateUserByEmail(
            @PathVariable String email,
            @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUserByEmail(email, request));
    }

    @PutMapping("/email/{email}/role")
    @Operation(summary = "Update user role by email", description = "Update user role by email address")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserResponse> updateUserRoleByEmail(
            @PathVariable String email,
            @RequestBody Map<String, String> request) {
        String roleStr = request.get("role");
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            return ResponseEntity.ok(userService.updateUserRoleByEmail(email, role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate user by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
