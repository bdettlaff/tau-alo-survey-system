package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.UserClassAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserClassAccessRepository extends JpaRepository<UserClassAccess, Long> {
    Optional<UserClassAccess> findByUserIdAndAccessCode(String userId, String accessCode);
    Optional<UserClassAccess> findFirstByAccessCode(String accessCode);
}