package com.portfolio.dto;

import com.portfolio.model.ProjectStatus;
import com.portfolio.model.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    @NotBlank(message = "El título es requerido")
    private String title;

    private String description;

    @NotNull(message = "El tipo de proyecto es requerido")
    private ProjectType projectType;

    private String roleInProject;
    private List<String> technologies;
    private String repoUrl;
    private String demoUrl;
    private String imageUrl;
    private ProjectStatus status;
}
