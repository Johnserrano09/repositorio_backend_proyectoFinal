package com.portfolio.controller;

import com.portfolio.dto.AdvisoryRequest;
import com.portfolio.dto.AdvisoryResponse;
import com.portfolio.security.UserPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/external")
@Tag(name = "External", description = "External user endpoints (EXTERNAL role required)")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ExternalController {

    private final AdvisoryService advisoryService;

    @GetMapping("/advisories")
    @Operation(summary = "List my advisories", description = "Get paginated list of own advisory requests")
    public ResponseEntity<Page<AdvisoryResponse>> listAdvisories(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        return ResponseEntity.ok(advisoryService.findByExternalId(principal.getId(), pageable));
    }

    @GetMapping("/advisories/{id}")
    @Operation(summary = "Get advisory", description = "Get advisory details by ID")
    public ResponseEntity<AdvisoryResponse> getAdvisory(@PathVariable UUID id) {
        return ResponseEntity.ok(advisoryService.findById(id));
    }

    @PostMapping("/advisories")
    @Operation(summary = "Request advisory", description = "Create a new advisory request")
    public ResponseEntity<AdvisoryResponse> createAdvisory(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AdvisoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(advisoryService.create(principal.getId(), request));
    }

    @PutMapping("/advisories/{id}/cancel")
    @Operation(summary = "Cancel advisory", description = "Cancel own pending advisory request")
    public ResponseEntity<AdvisoryResponse> cancelAdvisory(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(advisoryService.cancel(id, principal.getId()));
    }
}
