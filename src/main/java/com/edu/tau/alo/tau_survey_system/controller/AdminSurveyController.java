package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.ActiveSurveyOverviewDTO;
import com.edu.tau.alo.tau_survey_system.dto.CompositeSurveyRequest;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/surveys")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class AdminSurveyController {

    private final SurveyService surveyService;

    @Autowired
    public AdminSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/composite")
    public ResponseEntity<?> createCompositeSurvey(@RequestBody CompositeSurveyRequest request) {
        try {
            surveyService.saveCompositeSurveyStructure(request);
            return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Struktura ankiety została pomyślnie utworzona i zapisana.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Błąd podczas zapisu struktury ankiet: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveSurveyOverviewDTO>> getActiveSurveys() {
        List<ActiveSurveyOverviewDTO> activeSurveys = surveyService.getActiveSurveysForAdmin();
        return ResponseEntity.ok(activeSurveys);
    }
}