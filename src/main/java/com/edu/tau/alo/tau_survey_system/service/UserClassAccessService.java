package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.UserClassAccess;
import com.edu.tau.alo.tau_survey_system.repository.UserClassAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserClassAccessService {

    private final UserClassAccessRepository repo;
    private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

    public UserClassAccess registerOrGet(String userId, Long classId, String accessCode) {
        Optional<UserClassAccess> existing = repo.findFirstByAccessCode(accessCode);

        Instant firstUsedAt;
        Instant expiresAt;

        if (existing.isPresent()) {
            firstUsedAt = existing.get().getFirstUsedAt();
            expiresAt = existing.get().getExpiresAt();
        } else {
            firstUsedAt = Instant.now();
            expiresAt = firstUsedAt.plus(TOKEN_LIFETIME);
        }

        return repo.findByUserIdAndAccessCode(userId, accessCode)
                .orElseGet(() -> {
                    UserClassAccess entry = new UserClassAccess();
                    entry.setUserId(userId);
                    entry.setClassId(classId);
                    entry.setAccessCode(accessCode);
                    entry.setFirstUsedAt(firstUsedAt);
                    entry.setExpiresAt(expiresAt);
                    return repo.save(entry);
                });
    }

    public boolean isExpired(UserClassAccess access) {
        return Instant.now().isAfter(access.getExpiresAt());
    }
}