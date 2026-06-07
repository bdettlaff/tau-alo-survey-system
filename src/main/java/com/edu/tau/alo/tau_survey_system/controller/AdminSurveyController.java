package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.ActiveSurveyOverviewDTO;
import com.edu.tau.alo.tau_survey_system.dto.CompositeSurveyRequest;
import com.edu.tau.alo.tau_survey_system.dto.TokenInfoDTO;
import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.model.UserClassAccess;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import com.edu.tau.alo.tau_survey_system.service.UserClassAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/surveys")
public class AdminSurveyController {

    private final SurveyService surveyService;
    private final ClassRepository classRepository;
    private final UserClassAccessService userClassAccessService;

    @Autowired
    public AdminSurveyController(SurveyService surveyService,
                                 ClassRepository classRepository,
                                 UserClassAccessService userClassAccessService) {
        this.surveyService = surveyService;
        this.classRepository = classRepository;
        this.userClassAccessService = userClassAccessService;
    }

    // ── Tylko ADMIN ──────────────────────────────────────────────────────────

    @PreAuthorize("@userAuthService.hasRole(authentication, 'ADMIN')")
    @PostMapping("/composite")
    public ResponseEntity<?> createCompositeSurvey(@RequestBody CompositeSurveyRequest request) {
        try {
            surveyService.saveCompositeSurveyStructure(request);
            Classes updatedClass = classRepository.findById(request.getClassId()).orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Struktura ankiety została pomyślnie utworzona.",
                    "accessCode", updatedClass.getAccessCode()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Błąd podczas zapisu struktury ankiet: " + e.getMessage());
        }
    }

    @PreAuthorize("@userAuthService.hasRole(authentication, 'ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<ActiveSurveyOverviewDTO.SurveyEntryDTO>> getActiveSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveysForAdmin());
    }

    // ── Zalogowany użytkownik (student/admin) ────────────────────────────────

    @GetMapping("/active/by-code/{code}")
    public ResponseEntity<?> getActiveSurveysByCode(@PathVariable String code,
                                                    Principal principal) {
        try {
            List<ActiveSurveyOverviewDTO.SurveyEntryDTO> surveys =
                    surveyService.getActiveSurveysByCode(code);

            if (surveys.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String userId = extractUserId(principal);

            Classes schoolClass = classRepository
                    .findByAccessCode(code.toUpperCase())
                    .orElseThrow();

            UserClassAccess access = userClassAccessService
                    .registerOrGet(userId, schoolClass.getId(), code.toUpperCase());

            TokenInfoDTO tokenInfo = new TokenInfoDTO(
                    access.getFirstUsedAt(),
                    access.getExpiresAt(),
                    userClassAccessService.isExpired(access)
            );

            return ResponseEntity.ok(new ActiveSurveyOverviewDTO(surveys, tokenInfo));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Błąd serwera: " + e.getMessage());
        }
    }

    private String extractUserId(Principal principal) {
        if (principal instanceof JwtAuthenticationToken jwt) {
            String oid = jwt.getToken().getClaimAsString("oid");
            if (oid != null && !oid.isBlank()) return oid;
        }
        return principal != null ? principal.getName() : "anonymous";
    }
}