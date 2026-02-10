package com.portfolio.dto;

import com.portfolio.model.AdvisoryStatus;
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
public class AdvisoryResponse {
    private UUID id;
    private UserSummary programmer;
    private UserSummary external;
    private LocalDateTime scheduledAt;
    private AdvisoryStatus status;
    private String requestComment;
    private String responseMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private UUID id;
        private String name;
        private String email;
        private String avatarUrl;
    }
}
