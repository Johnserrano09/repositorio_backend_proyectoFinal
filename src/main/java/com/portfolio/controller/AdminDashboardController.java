package com.portfolio.controller;

import com.portfolio.dto.DashboardStats;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Dashboard", description = "Dashboard and statistics endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get global dashboard", description = "Get global statistics for admin")
    public ResponseEntity<DashboardStats> getGlobalDashboard() {
        return ResponseEntity.ok(dashboardService.getGlobalStats());
    }
}
