package com.portfolio.controller;

import com.lowagie.text.DocumentException;
import com.portfolio.model.AdvisoryStatus;
import com.portfolio.model.ProjectStatus;
import com.portfolio.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reports")
@Tag(name = "Admin Reports", description = "Administrative report endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/advisories/pdf")
    @Operation(summary = "Export advisories PDF", description = "Generate PDF report of advisories with filters")
    public ResponseEntity<byte[]> exportAdvisoriesPdf(
            @RequestParam(required = false) UUID programmerId,
            @RequestParam(required = false) AdvisoryStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws DocumentException {
        byte[] pdf = reportService.generateAdvisoriesPdfForAdmin(programmerId, status, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asesorias-admin.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/advisories/excel")
    @Operation(summary = "Export advisories Excel", description = "Generate Excel report of advisories with filters")
    public ResponseEntity<byte[]> exportAdvisoriesExcel(
            @RequestParam(required = false) UUID programmerId,
            @RequestParam(required = false) AdvisoryStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws IOException {
        byte[] excel = reportService.generateAdvisoriesExcelForAdmin(programmerId, status, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asesorias-admin.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/projects/pdf")
    @Operation(summary = "Export projects PDF", description = "Generate PDF report of projects with filters")
    public ResponseEntity<byte[]> exportProjectsPdf(
            @RequestParam(required = false) UUID programmerId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws DocumentException {
        byte[] pdf = reportService.generateProjectsPdfForAdmin(programmerId, status, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proyectos-admin.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/projects/excel")
    @Operation(summary = "Export projects Excel", description = "Generate Excel report of projects with filters")
    public ResponseEntity<byte[]> exportProjectsExcel(
            @RequestParam(required = false) UUID programmerId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) throws IOException {
        byte[] excel = reportService.generateProjectsExcelForAdmin(programmerId, status, startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proyectos-admin.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
