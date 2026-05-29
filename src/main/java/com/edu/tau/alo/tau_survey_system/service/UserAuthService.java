package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.User;
import com.edu.tau.alo.tau_survey_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service("userAuthService")
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;

    /**
     * Używany w @PreAuthorize("@userAuthService.hasRole(authentication, 'ADMIN')")
     * Sprawdza rolę użytkownika w lokalnej bazie danych na podstawie OID z tokena Azure AD.
     */
    public boolean hasRole(Authentication authentication, String role) {
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String oid = jwt.getClaimAsString("oid");
            if (oid == null) return false;

            return userRepository.findByMicrosoftOid(oid)
                    .map(user -> user.getRole().name().equals(role))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}