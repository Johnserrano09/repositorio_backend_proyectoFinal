package com.portfolio.dto;

import com.portfolio.model.ProjectStatus;
import com.portfolio.model.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String title;
    private String description;
    private ProjectType projectType;
    private String roleInProject;
    private List<String> technologies;
    private String repoUrl;
    private String demoUrl;
    private String imageUrl;
    private ProjectStatus status;
    private LocalDateTime createdAt;
}
