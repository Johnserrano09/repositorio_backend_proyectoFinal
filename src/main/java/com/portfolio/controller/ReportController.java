package com.portfolio.controller;

import com.lowagie.text.DocumentException;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/api/programmer/reports")
@Tag(name = "Reports", description = "Report generation endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/advisories/pdf")
    @Operation(summary = "Export advisories PDF", description = "Generate PDF report of advisories")
    public ResponseEntity<byte[]> exportAdvisoriesPdf(@AuthenticationPrincipal UserPrincipal principal)
            throws DocumentException {
        byte[] pdf = reportService.generateAdvisoriesPdf(principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asesorias.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/advisories/excel")
    @Operation(summary = "Export advisories Excel", description = "Generate Excel report of advisories")
    public ResponseEntity<byte[]> exportAdvisoriesExcel(@AuthenticationPrincipal UserPrincipal principal)
            throws IOException {
        byte[] excel = reportService.generateAdvisoriesExcel(principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asesorias.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/projects/pdf")
    @Operation(summary = "Export projects PDF", description = "Generate PDF report of projects")
    public ResponseEntity<byte[]> exportProjectsPdf(@AuthenticationPrincipal UserPrincipal principal)
            throws DocumentException {
        byte[] pdf = reportService.generateProjectsPdf(principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proyectos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/projects/excel")
    @Operation(summary = "Export projects Excel", description = "Generate Excel report of projects")
    public ResponseEntity<byte[]> exportProjectsExcel(@AuthenticationPrincipal UserPrincipal principal)
            throws IOException {
        byte[] excel = reportService.generateProjectsExcel(principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proyectos.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
