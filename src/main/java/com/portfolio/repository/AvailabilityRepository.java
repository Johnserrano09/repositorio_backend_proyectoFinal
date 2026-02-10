package com.portfolio.repository;

import com.portfolio.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findByUserIdAndIsActiveTrue(UUID userId);

    List<Availability> findByUserIdAndDayOfWeekAndIsActiveTrue(UUID userId, DayOfWeek dayOfWeek);

    void deleteByUserIdAndId(UUID userId, UUID id);
}
