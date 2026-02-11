package com.portfolio.controller;

import com.portfolio.dto.UserResponse;
import com.portfolio.model.Role;
import com.portfolio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    @Operation(summary = "Get user", description = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(java.util.UUID.fromString(id)));
    }
}
