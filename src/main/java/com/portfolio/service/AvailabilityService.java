package com.portfolio.service;

import com.portfolio.dto.AvailabilityRequest;
import com.portfolio.dto.AvailabilityResponse;
import com.portfolio.exception.BadRequestException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.model.Availability;
import com.portfolio.model.User;
import com.portfolio.repository.AvailabilityRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public List<AvailabilityResponse> findByUserId(UUID userId) {
        return availabilityRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AvailabilityResponse create(UUID userId, AvailabilityRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime())) {
            throw new BadRequestException("La hora de inicio debe ser antes de la hora de fin");
        }

        Availability availability = Availability.builder()
                .user(user)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();

        Availability saved = availabilityRepository.save(availability);
        log.info("Created availability for user: {} on {}", userId, request.getDayOfWeek());
        return mapToResponse(saved);
    }

    public AvailabilityResponse update(UUID availabilityId, UUID userId, AvailabilityRequest request) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad", "id", availabilityId));

        // Check ownership
        if (!availability.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para editar esta disponibilidad");
        }

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime())) {
            throw new BadRequestException("La hora de inicio debe ser antes de la hora de fin");
        }

        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        Availability saved = availabilityRepository.save(availability);
        log.info("Updated availability: {}", availabilityId);
        return mapToResponse(saved);
    }

    public void delete(UUID availabilityId, UUID userId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad", "id", availabilityId));

        // Check ownership
        if (!availability.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta disponibilidad");
        }

        availability.setIsActive(false);
        availabilityRepository.save(availability);
        log.info("Deactivated availability: {}", availabilityId);
    }

    private AvailabilityResponse mapToResponse(Availability availability) {
        return AvailabilityResponse.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .isActive(availability.getIsActive())
                .build();
    }
}
