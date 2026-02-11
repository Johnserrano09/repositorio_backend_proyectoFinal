package com.portfolio.controller;

import com.portfolio.dto.PortfolioResponse;
import com.portfolio.dto.UserResponse;
import com.portfolio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public", description = "Public endpoints (no authentication required)")
@RequiredArgsConstructor
public class PublicController {

    private final UserService userService;

    @GetMapping("/programmers")
    @Operation(summary = "List programmers", description = "Get paginated list of active programmers")
    public ResponseEntity<Page<UserResponse>> listProgrammers(Pageable pageable) {
        return ResponseEntity.ok(userService.findProgrammers(pageable));
    }

    @GetMapping("/programmers/{id}")
    @Operation(summary = "Get programmer", description = "Get programmer details by ID")
    public ResponseEntity<UserResponse> getProgrammer(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/programmers/{id}/portfolio")
    @Operation(summary = "Get portfolio", description = "Get complete portfolio with projects and availability")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getPortfolio(id));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user", description = "Get user details by ID (including availability for programmers)")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
