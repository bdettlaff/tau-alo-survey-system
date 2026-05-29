package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(
        name = "user_class_access",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_code",
                columnNames = {"user_id", "access_code"}
        )
)
public class UserClassAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "access_code", nullable = false, length = 50)
    private String accessCode;

    @Column(name = "first_used_at", nullable = false, updatable = false)
    private Instant firstUsedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}