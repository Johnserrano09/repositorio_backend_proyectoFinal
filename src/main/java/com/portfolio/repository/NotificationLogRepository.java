package com.portfolio.repository;

import com.portfolio.model.NotificationLog;
import com.portfolio.model.NotificationStatus;
import com.portfolio.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    Page<NotificationLog> findByUserId(UUID userId, Pageable pageable);

    List<NotificationLog> findByStatus(NotificationStatus status);

    List<NotificationLog> findByTypeAndStatus(NotificationType type, NotificationStatus status);

    long countByType(NotificationType type);

    long countByStatus(NotificationStatus status);
}
