package com.portfolio.repository;

import com.portfolio.model.RefreshToken;
import com.portfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserAndRevokedAtIsNull(User user);

    List<RefreshToken> findByExpiresAtBefore(LocalDateTime cutoff);
}
