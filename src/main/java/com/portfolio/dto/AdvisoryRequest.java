package com.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisoryRequest {
    @NotNull(message = "El programador es requerido")
    private UUID programmerId;

    @NotNull(message = "La fecha y hora es requerida")
    private LocalDateTime scheduledAt;

    private String comment;
}
