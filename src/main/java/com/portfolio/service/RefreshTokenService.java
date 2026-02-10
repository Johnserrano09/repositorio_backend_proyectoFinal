package com.portfolio.service;

import com.portfolio.exception.BadRequestException;
import com.portfolio.model.RefreshToken;
import com.portfolio.model.User;
import com.portfolio.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public RefreshToken createToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusNanos(refreshExpirationMillis * 1_000_000L))
                .build();

        RefreshToken saved = refreshTokenRepository.save(token);
        log.info("Created refresh token for user: {}", user.getEmail());
        return saved;
    }

    public RefreshToken validateToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Refresh token inválido"));

        if (token.getRevokedAt() != null) {
            throw new BadRequestException("Refresh token revocado");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expirado");
        }

        return token;
    }

    public RefreshToken rotateToken(RefreshToken oldToken) {
        oldToken.setRevokedAt(LocalDateTime.now());

        RefreshToken newToken = createToken(oldToken.getUser());
        oldToken.setReplacedByToken(newToken.getToken());
        refreshTokenRepository.save(oldToken);

        return newToken;
    }

    public void revokeToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Refresh token inválido"));

        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    public void revokeAllForUser(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndRevokedAtIsNull(user);
        for (RefreshToken token : tokens) {
            token.setRevokedAt(LocalDateTime.now());
        }
        refreshTokenRepository.saveAll(tokens);
    }
}
