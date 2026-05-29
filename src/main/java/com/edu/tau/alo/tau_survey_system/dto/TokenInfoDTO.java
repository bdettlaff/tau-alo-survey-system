package com.edu.tau.alo.tau_survey_system.dto;

import java.time.Instant;

public class TokenInfoDTO {

    private Instant firstUsedAt;
    private Instant expiresAt;
    private boolean expired;

    public TokenInfoDTO(Instant firstUsedAt, Instant expiresAt, boolean expired) {
        this.firstUsedAt = firstUsedAt;
        this.expiresAt = expiresAt;
        this.expired = expired;
    }

    public Instant getFirstUsedAt() { return firstUsedAt; }
    public void setFirstUsedAt(Instant firstUsedAt) { this.firstUsedAt = firstUsedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}