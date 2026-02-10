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
@RequestMapping("/api/programmer/dashboard")
@Tag(name = "Dashboard", description = "Programmer dashboard endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ProgrammerDashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get my dashboard", description = "Get personal statistics for programmer")
    public ResponseEntity<DashboardStats> getMyDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dashboardService.getProgrammerStats(principal.getId()));
    }
}
