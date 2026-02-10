package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.model.AdvisoryStatus;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.AdvisoryService;
import com.portfolio.service.AvailabilityService;
import com.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/programmer")
@Tag(name = "Programmer", description = "Programmer endpoints (PROGRAMMER role required)")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ProgrammerController {

    private final ProjectService projectService;
    private final AvailabilityService availabilityService;
    private final AdvisoryService advisoryService;

    // ============ PROJECTS ============

    @GetMapping("/projects")
    @Operation(summary = "List my projects", description = "Get paginated list of own projects")
    public ResponseEntity<Page<ProjectResponse>> listProjects(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        return ResponseEntity.ok(projectService.findByUserId(principal.getId(), pageable));
    }

    @GetMapping("/projects/{id}")
    @Operation(summary = "Get project", description = "Get project by ID")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping("/projects")
    @Operation(summary = "Create project", description = "Create a new project")
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(principal.getId(), request));
    }

    @PutMapping("/projects/{id}")
    @Operation(summary = "Update project", description = "Update own project")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.update(id, principal.getId(), request));
    }

    @DeleteMapping("/projects/{id}")
    @Operation(summary = "Delete project", description = "Delete own project")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        projectService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    // ============ AVAILABILITY ============

    @GetMapping("/availability")
    @Operation(summary = "List my availability", description = "Get list of own availability slots")
    public ResponseEntity<List<AvailabilityResponse>> listAvailability(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(availabilityService.findByUserId(principal.getId()));
    }

    @PostMapping("/availability")
    @Operation(summary = "Add availability", description = "Add a new availability slot")
    public ResponseEntity<AvailabilityResponse> addAvailability(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.create(principal.getId(), request));
    }

    @PutMapping("/availability/{id}")
    @Operation(summary = "Update availability", description = "Update own availability slot")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.update(id, principal.getId(), request));
    }

    @DeleteMapping("/availability/{id}")
    @Operation(summary = "Delete availability", description = "Delete own availability slot")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        availabilityService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    // ============ ADVISORIES ============

    @GetMapping("/advisories")
    @Operation(summary = "List received advisories", description = "Get paginated list of advisory requests")
    public ResponseEntity<Page<AdvisoryResponse>> listAdvisories(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) AdvisoryStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(advisoryService.findByProgrammerId(principal.getId(), status, pageable));
    }

    @GetMapping("/advisories/{id}")
    @Operation(summary = "Get advisory", description = "Get advisory by ID")
    public ResponseEntity<AdvisoryResponse> getAdvisory(@PathVariable UUID id) {
        return ResponseEntity.ok(advisoryService.findById(id));
    }

    @PutMapping("/advisories/{id}/approve")
    @Operation(summary = "Approve advisory", description = "Approve a pending advisory request")
    public ResponseEntity<AdvisoryResponse> approveAdvisory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AdvisoryActionRequest request) {
        return ResponseEntity.ok(advisoryService.approve(id, principal.getId(), request));
    }

    @PutMapping("/advisories/{id}/reject")
    @Operation(summary = "Reject advisory", description = "Reject a pending advisory request")
    public ResponseEntity<AdvisoryResponse> rejectAdvisory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AdvisoryActionRequest request) {
        return ResponseEntity.ok(advisoryService.reject(id, principal.getId(), request));
    }

    @PutMapping("/advisories/{id}/complete")
    @Operation(summary = "Complete advisory", description = "Mark an approved advisory as completed")
    public ResponseEntity<AdvisoryResponse> completeAdvisory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(advisoryService.complete(id, principal.getId()));
    }
}
