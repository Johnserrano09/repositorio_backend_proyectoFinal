package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Map<String, Long> advisoryCountByStatus;
    private List<TimeSeriesData> advisoryTimeSeries;
    private List<TechnologyCount> topTechnologies;
    private List<UserCount> advisoriesByProgrammer;
    private List<UserCount> projectsByProgrammer;
    private long totalProjects;
    private long totalAdvisories;
    private long pendingAdvisories;
    private long completedAdvisories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesData {
        private String date;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnologyCount {
        private String technology;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCount {
        private String userId;
        private String name;
        private Long count;
    }
}
