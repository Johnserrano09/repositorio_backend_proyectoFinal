package com.portfolio.service;

import com.portfolio.dto.*;
import com.portfolio.exception.BadRequestException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.model.*;
import com.portfolio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AvailabilityRepository availabilityRepository;

    public Page<UserResponse> findProgrammers(Pageable pageable) {
        return userRepository.findByRoleAndIsActiveTrue(Role.PROGRAMMER, pageable)
                .map(this::mapToResponse);
    }

    public Page<UserResponse> findAllUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapToResponse(user);
    }

    public PortfolioResponse getPortfolio(UUID programmerId) {
        User user = userRepository.findById(programmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Programador", "id", programmerId));

        if (user.getRole() != Role.PROGRAMMER) {
            throw new BadRequestException("El usuario no es un programador");
        }

        List<Project> projects = projectRepository.findByUserIdOrderByCreatedAtDesc(programmerId);
        List<Availability> availabilities = availabilityRepository.findByUserIdAndIsActiveTrue(programmerId);

        return PortfolioResponse.builder()
                .programmer(mapToResponse(user))
                .projects(projects.stream().map(this::mapProjectToResponse).toList())
                .availability(availabilities.stream().map(this::mapAvailabilityToResponse).toList())
                .build();
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario con ese email");
        }

        // Use email as name if name is not provided
        String userName = (request.getName() != null && !request.getName().isBlank()) 
            ? request.getName() 
            : request.getEmail().split("@")[0];

        User user = User.builder()
                .email(request.getEmail())
                .name(userName)
                .phone(request.getPhone())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Created user: {} with role: {}", saved.getEmail(), saved.getRole());
        return mapToResponse(saved);
    }

    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());
        user.setAvatarUrl(request.getAvatarUrl());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User saved = userRepository.save(user);
        log.info("Updated user: {}", saved.getEmail());
        return mapToResponse(saved);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Deactivated user: {}", user.getEmail());
    }

    private UserResponse mapToResponse(User user) {
        long projectCount = projectRepository.countByUserId(user.getId());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .projectCount(projectCount)
                .build();
    }

    private ProjectResponse mapProjectToResponse(Project project) {
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

    private AvailabilityResponse mapAvailabilityToResponse(Availability availability) {
        return AvailabilityResponse.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .isActive(availability.getIsActive())
                .build();
    }

    public List<UserResponse> findByRole(Role role) {
        return userRepository.findByRoleAndIsActiveTrue(role)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<UserResponse> findAllActive() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse findByEmailResponse(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return mapToResponse(user);
    }

    public UserResponse updateUserByEmail(String email, UserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());
        user.setAvatarUrl(request.getAvatarUrl());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User saved = userRepository.save(user);
        log.info("Updated user by email: {}", saved.getEmail());
        return mapToResponse(saved);
    }

    public UserResponse updateUserRoleByEmail(String email, Role role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("Updated role for user: {} to {}", saved.getEmail(), role);
        return mapToResponse(saved);
    }
}
