package com.portfolio.service;

import com.portfolio.dto.ProjectRequest;
import com.portfolio.dto.ProjectResponse;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.model.Project;
import com.portfolio.model.ProjectStatus;
import com.portfolio.model.User;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Page<ProjectResponse> findByUserId(UUID userId, Pageable pageable) {
        return projectRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    public List<ProjectResponse> findAllByUserId(UUID userId) {
        return projectRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProjectResponse findById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", id));
        return mapToResponse(project);
    }

    public ProjectResponse create(UUID userId, ProjectRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        Project project = Project.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .projectType(request.getProjectType())
                .roleInProject(request.getRoleInProject())
                .technologies(
                        request.getTechnologies() != null ? request.getTechnologies().toArray(new String[0]) : null)
                .repoUrl(request.getRepoUrl())
                .demoUrl(request.getDemoUrl())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.IN_PROGRESS)
                .build();

        Project saved = projectRepository.save(project);
        log.info("Created project: {} for user: {}", saved.getTitle(), userId);
        return mapToResponse(saved);
    }

    public ProjectResponse update(UUID projectId, UUID userId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", projectId));

        // Check ownership
        if (!project.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para editar este proyecto");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setProjectType(request.getProjectType());
        project.setRoleInProject(request.getRoleInProject());
        project.setTechnologies(
                request.getTechnologies() != null ? request.getTechnologies().toArray(new String[0]) : null);
        project.setRepoUrl(request.getRepoUrl());
        project.setDemoUrl(request.getDemoUrl());
        project.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        Project saved = projectRepository.save(project);
        log.info("Updated project: {}", saved.getTitle());
        return mapToResponse(saved);
    }

    public void delete(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", projectId));

        // Check ownership
        if (!project.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este proyecto");
        }

        projectRepository.delete(project);
        log.info("Deleted project: {}", projectId);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .roleInProject(project.getRoleInProject())
                .technologies(project.getTechnologies() != null ? Arrays.asList(project.getTechnologies()) : List.of())
                .repoUrl(project.getRepoUrl())
                .demoUrl(project.getDemoUrl())
                .imageUrl(project.getImageUrl())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
