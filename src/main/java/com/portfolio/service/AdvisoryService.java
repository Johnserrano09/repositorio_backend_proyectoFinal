package com.portfolio.service;

import com.portfolio.dto.AdvisoryActionRequest;
import com.portfolio.dto.AdvisoryRequest;
import com.portfolio.dto.AdvisoryResponse;
import com.portfolio.exception.BadRequestException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.model.*;
import com.portfolio.repository.AdvisoryRepository;
import com.portfolio.repository.AvailabilityRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdvisoryService {

    private final AdvisoryRepository advisoryRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final NotificationService notificationService;

    public Page<AdvisoryResponse> findAll(AdvisoryStatus status, Pageable pageable) {
        if (status != null) {
            return advisoryRepository.findByStatus(status, pageable)
                    .map(this::mapToResponse);
        }
        return advisoryRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<AdvisoryResponse> findByProgrammerId(UUID programmerId, AdvisoryStatus status, Pageable pageable) {
        if (status != null) {
            return advisoryRepository.findByProgrammerIdAndStatus(programmerId, status, pageable)
                    .map(this::mapToResponse);
        }
        return advisoryRepository.findByProgrammerId(programmerId, pageable)
                .map(this::mapToResponse);
    }

    public Page<AdvisoryResponse> findByExternalId(UUID externalId, Pageable pageable) {
        return advisoryRepository.findByExternalId(externalId, pageable)
                .map(this::mapToResponse);
    }

    public AdvisoryResponse findById(UUID id) {
        Advisory advisory = advisoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asesoría", "id", id));
        return mapToResponse(advisory);
    }

    public AdvisoryResponse create(UUID externalId, AdvisoryRequest request) {
        User external = userRepository.findById(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", externalId));

        User programmer = userRepository.findById(request.getProgrammerId())
                .orElseThrow(() -> new ResourceNotFoundException("Programador", "id", request.getProgrammerId()));

        if (programmer.getRole() != Role.PROGRAMMER) {
            throw new BadRequestException("El usuario seleccionado no es un programador");
        }

        // Validate scheduled time is in the future
        if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la asesoría debe ser en el futuro");
        }

        // Validate availability
        validateAvailability(programmer.getId(), request.getScheduledAt());

        // Check for conflicts
        validateNoConflicts(programmer.getId(), request.getScheduledAt());

        Advisory advisory = Advisory.builder()
                .programmer(programmer)
                .external(external)
                .scheduledAt(request.getScheduledAt())
                .status(AdvisoryStatus.PENDING)
                .requestComment(request.getComment())
                .build();

        Advisory saved = advisoryRepository.save(advisory);
        log.info("Created advisory {} from {} to {} at {}",
                saved.getId(), external.getEmail(), programmer.getEmail(), saved.getScheduledAt());

        notificationService.sendAdvisoryRequestNotification(programmer, external, saved);

        return mapToResponse(saved);
    }

    public AdvisoryResponse approve(UUID advisoryId, UUID programmerId, AdvisoryActionRequest request) {
        Advisory advisory = advisoryRepository.findById(advisoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesoría", "id", advisoryId));

        // Check ownership
        if (!advisory.getProgrammer().getId().equals(programmerId)) {
            throw new AccessDeniedException("No tienes permiso para aprobar esta asesoría");
        }

        if (advisory.getStatus() != AdvisoryStatus.PENDING) {
            throw new BadRequestException("Solo se pueden aprobar asesorías pendientes");
        }

        advisory.setStatus(AdvisoryStatus.APPROVED);
        advisory.setResponseMessage(request.getMessage());

        Advisory saved = advisoryRepository.save(advisory);
        log.info("Approved advisory: {}", advisoryId);

        notificationService.sendAdvisoryApprovedNotification(advisory.getExternal(), advisory.getProgrammer(), saved);

        return mapToResponse(saved);
    }

    public AdvisoryResponse reject(UUID advisoryId, UUID programmerId, AdvisoryActionRequest request) {
        Advisory advisory = advisoryRepository.findById(advisoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesoría", "id", advisoryId));

        // Check ownership
        if (!advisory.getProgrammer().getId().equals(programmerId)) {
            throw new AccessDeniedException("No tienes permiso para rechazar esta asesoría");
        }

        if (advisory.getStatus() != AdvisoryStatus.PENDING) {
            throw new BadRequestException("Solo se pueden rechazar asesorías pendientes");
        }

        advisory.setStatus(AdvisoryStatus.REJECTED);
        advisory.setResponseMessage(request.getMessage());

        Advisory saved = advisoryRepository.save(advisory);
        log.info("Rejected advisory: {}", advisoryId);

        notificationService.sendAdvisoryRejectedNotification(advisory.getExternal(), advisory.getProgrammer(), saved);

        return mapToResponse(saved);
    }

    public AdvisoryResponse cancel(UUID advisoryId, UUID userId) {
        Advisory advisory = advisoryRepository.findById(advisoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesoría", "id", advisoryId));

        // Check ownership (only external can cancel their own)
        if (!advisory.getExternal().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para cancelar esta asesoría");
        }

        if (advisory.getStatus() != AdvisoryStatus.PENDING) {
            throw new BadRequestException("Solo se pueden cancelar asesorías pendientes");
        }

        advisory.setStatus(AdvisoryStatus.CANCELLED);

        Advisory saved = advisoryRepository.save(advisory);
        log.info("Cancelled advisory: {}", advisoryId);

        return mapToResponse(saved);
    }

    public AdvisoryResponse complete(UUID advisoryId, UUID programmerId) {
        Advisory advisory = advisoryRepository.findById(advisoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesoría", "id", advisoryId));

        // Check ownership
        if (!advisory.getProgrammer().getId().equals(programmerId)) {
            throw new AccessDeniedException("No tienes permiso para completar esta asesoría");
        }

        if (advisory.getStatus() != AdvisoryStatus.APPROVED) {
            throw new BadRequestException("Solo se pueden completar asesorías aprobadas");
        }

        advisory.setStatus(AdvisoryStatus.COMPLETED);

        Advisory saved = advisoryRepository.save(advisory);
        log.info("Completed advisory: {}", advisoryId);

        return mapToResponse(saved);
    }

    private void validateAvailability(UUID programmerId, LocalDateTime scheduledAt) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime time = scheduledAt.toLocalTime();

        List<Availability> availabilities = availabilityRepository
                .findByUserIdAndDayOfWeekAndIsActiveTrue(programmerId, dayOfWeek);

        boolean isAvailable = availabilities.stream()
                .anyMatch(a -> !time.isBefore(a.getStartTime()) && !time.isAfter(a.getEndTime().minusMinutes(30)));

        if (!isAvailable) {
            throw new BadRequestException("El programador no está disponible en ese horario");
        }
    }

    private void validateNoConflicts(UUID programmerId, LocalDateTime scheduledAt) {
        List<Advisory> conflicts = advisoryRepository.findConflictingAdvisories(programmerId, scheduledAt);

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Ya existe una asesoría programada en ese horario");
        }
    }

    private AdvisoryResponse mapToResponse(Advisory advisory) {
        return AdvisoryResponse.builder()
                .id(advisory.getId())
                .programmer(mapUserSummary(advisory.getProgrammer()))
                .external(mapUserSummary(advisory.getExternal()))
                .scheduledAt(advisory.getScheduledAt())
                .status(advisory.getStatus())
                .requestComment(advisory.getRequestComment())
                .responseMessage(advisory.getResponseMessage())
                .createdAt(advisory.getCreatedAt())
                .updatedAt(advisory.getUpdatedAt())
                .build();
    }

    private AdvisoryResponse.UserSummary mapUserSummary(User user) {
        return AdvisoryResponse.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
