package com.portfolio.repository;

import com.portfolio.model.Project;
import com.portfolio.model.ProjectStatus;
import com.portfolio.model.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Page<Project> findByUserId(UUID userId, Pageable pageable);

    Page<Project> findByUserIdAndProjectType(UUID userId, ProjectType type, Pageable pageable);

    Page<Project> findByUserIdAndStatus(UUID userId, ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND p.user.isActive = true")
    List<Project> findActiveProjectsByUserId(UUID userId);

    long countByUserId(UUID userId);

    @Query("SELECT p.user.id, p.user.name, COUNT(p) FROM Project p GROUP BY p.user.id, p.user.name")
    List<Object[]> countByProgrammer();

        @Query("SELECT p FROM Project p " +
            "WHERE (:userId IS NULL OR p.user.id = :userId) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:start IS NULL OR p.createdAt >= :start) " +
            "AND (:end IS NULL OR p.createdAt <= :end) " +
            "ORDER BY p.createdAt DESC")
        List<Project> findByFilters(UUID userId, ProjectStatus status, java.time.LocalDateTime start,
            java.time.LocalDateTime end);
}
