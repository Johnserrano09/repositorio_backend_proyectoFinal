package com.portfolio.service;

import com.portfolio.dto.DashboardStats;
import com.portfolio.model.Project;
import com.portfolio.repository.AdvisoryRepository;
import com.portfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final AdvisoryRepository advisoryRepository;
    private final ProjectRepository projectRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DashboardStats getGlobalStats() {
        return buildStats(null);
    }

    public DashboardStats getProgrammerStats(UUID programmerId) {
        return buildStats(programmerId);
    }

    private DashboardStats buildStats(UUID programmerId) {
        // Advisory count by status
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> rawCounts = programmerId != null
                ? advisoryRepository.countByStatusForProgrammer(programmerId)
                : advisoryRepository.countByStatus();

        for (Object[] row : rawCounts) {
            statusCounts.put(row[0].toString(), (Long) row[1]);
        }

        // Time series (last 30 days)
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> rawTimeSeries = advisoryRepository.countByDateSince(since);
        List<DashboardStats.TimeSeriesData> timeSeries = new ArrayList<>();

        for (Object[] row : rawTimeSeries) {
            timeSeries.add(DashboardStats.TimeSeriesData.builder()
                    .date(row[0].toString())
                    .count((Long) row[1])
                    .build());
        }

        // Top technologies
        List<Project> projects = programmerId != null
                ? projectRepository.findByUserIdOrderByCreatedAtDesc(programmerId)
                : projectRepository.findAll();

        Map<String, Long> techCounts = new HashMap<>();
        for (Project project : projects) {
            if (project.getTechnologies() != null) {
                for (String tech : project.getTechnologies()) {
                    techCounts.merge(tech, 1L, Long::sum);
                }
            }
        }

        List<DashboardStats.TechnologyCount> topTech = techCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> DashboardStats.TechnologyCount.builder()
                        .technology(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        // Summary counts
        long totalProjects = projects.size();
        long totalAdvisories = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        long pending = statusCounts.getOrDefault("PENDING", 0L);
        long completed = statusCounts.getOrDefault("COMPLETED", 0L);

        List<DashboardStats.UserCount> advisoriesByProgrammer = null;
        List<DashboardStats.UserCount> projectsByProgrammer = null;

        if (programmerId == null) {
            advisoriesByProgrammer = advisoryRepository.countByProgrammer().stream()
                .map(row -> DashboardStats.UserCount.builder()
                    .userId(row[0].toString())
                    .name(row[1].toString())
                    .count((Long) row[2])
                    .build())
                .toList();

            projectsByProgrammer = projectRepository.countByProgrammer().stream()
                .map(row -> DashboardStats.UserCount.builder()
                    .userId(row[0].toString())
                    .name(row[1].toString())
                    .count((Long) row[2])
                    .build())
                .toList();
        }

        return DashboardStats.builder()
                .advisoryCountByStatus(statusCounts)
                .advisoryTimeSeries(timeSeries)
                .topTechnologies(topTech)
                .advisoriesByProgrammer(advisoriesByProgrammer)
                .projectsByProgrammer(projectsByProgrammer)
                .totalProjects(totalProjects)
                .totalAdvisories(totalAdvisories)
                .pendingAdvisories(pending)
                .completedAdvisories(completed)
                .build();
    }
}
