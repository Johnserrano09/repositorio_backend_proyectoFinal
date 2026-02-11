package com.portfolio.controller;

import com.portfolio.dto.UserRequest;
import com.portfolio.dto.UserResponse;
import com.portfolio.dto.AdvisoryResponse;
import com.portfolio.model.AdvisoryStatus;
import com.portfolio.service.UserService;
import com.portfolio.service.AdvisoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin endpoints (ADMIN role required)")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdvisoryService advisoryService;

    @GetMapping("/programmers")
    @Operation(summary = "List all programmers", description = "Get paginated list of programmers (Admin only)")
    public ResponseEntity<Page<UserResponse>> listProgrammers(Pageable pageable) {
        return ResponseEntity.ok(userService.findProgrammers(pageable));
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Get paginated list of all users (Admin only)")
    public ResponseEntity<Page<UserResponse>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @GetMapping("/programmers/{id}")
    @Operation(summary = "Get programmer", description = "Get programmer by ID")
    public ResponseEntity<UserResponse> getProgrammer(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/programmers")
    @Operation(summary = "Create programmer", description = "Create a new programmer user")
    public ResponseEntity<UserResponse> createProgrammer(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/programmers/{id}")
    @Operation(summary = "Update programmer", description = "Update programmer details")
    public ResponseEntity<UserResponse> updateProgrammer(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/programmers/{id}")
    @Operation(summary = "Delete programmer", description = "Deactivate programmer (soft delete)")
    public ResponseEntity<Void> deleteProgrammer(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ============ ADVISORIES ============

    @GetMapping("/advisories")
    @Operation(summary = "List all advisories", description = "Get paginated list of all advisories (Admin only)")
    public ResponseEntity<Page<AdvisoryResponse>> listAdvisories(
            @RequestParam(required = false) AdvisoryStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(advisoryService.findAll(status, pageable));
    }

    @GetMapping("/advisories/{id}")
    @Operation(summary = "Get advisory", description = "Get advisory by ID")
    public ResponseEntity<AdvisoryResponse> getAdvisory(@PathVariable UUID id) {
        return ResponseEntity.ok(advisoryService.findById(id));
    }
}
