package com.portfolio.controller;

import com.portfolio.dto.AuthResponse;
import com.portfolio.dto.GoogleAuthRequest;
import com.portfolio.dto.RefreshTokenRequest;
import com.portfolio.model.Role;
import com.portfolio.model.User;
import com.portfolio.repository.UserRepository;
import com.portfolio.security.GoogleAuthService;
import com.portfolio.security.JwtService;
import com.portfolio.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final GoogleAuthService googleAuthService;
    private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @PostMapping("/google")
    @Operation(summary = "Authenticate with Google", description = "Exchange Firebase/Google ID token for JWT")
    public ResponseEntity<AuthResponse> authenticateWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        log.info("Processing Google authentication request");

        var googleUser = googleAuthService.verifyIdToken(request.getIdToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de Google inválido"));

        // Find or create user
        User user = userRepository.findByEmail(googleUser.email())
                .orElseGet(() -> createNewUser(googleUser));

        // Update Firebase UID if not set
        if (user.getFirebaseUid() == null) {
            user.setFirebaseUid(googleUser.uid());
            user = userRepository.save(user);
        }

        // Generate JWT access token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId().toString());

        // Issue refresh token
        String refreshToken = refreshTokenService.createToken(user).getToken();

        log.info("User {} authenticated successfully with role {}", user.getEmail(), user.getRole());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT", description = "Exchange refresh token for new access token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        var refreshToken = refreshTokenService.validateToken(request.getRefreshToken());

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId().toString());

        String newRefreshToken = refreshTokenService.rotateToken(refreshToken).getToken();

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    private User createNewUser(GoogleAuthService.GoogleUserInfo googleUser) {
        log.info("Creating new user for email: {}", googleUser.email());

        User newUser = User.builder()
                .email(googleUser.email())
                .name(googleUser.name() != null ? googleUser.name() : googleUser.email())
                .avatarUrl(googleUser.pictureUrl())
                .firebaseUid(googleUser.uid())
                .role(Role.USER) // Default role for new users
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }
}
