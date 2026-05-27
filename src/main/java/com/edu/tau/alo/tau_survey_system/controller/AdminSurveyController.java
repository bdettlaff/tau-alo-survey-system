package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.ActiveSurveyOverviewDTO;
import com.edu.tau.alo.tau_survey_system.dto.CompositeSurveyRequest;
import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/surveys")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class AdminSurveyController {

    private final SurveyService surveyService;
    private final ClassRepository classRepository;

    @Autowired
    public AdminSurveyController(SurveyService surveyService, ClassRepository classRepository) {
        this.surveyService = surveyService;
        this.classRepository = classRepository;
    }

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
            return ResponseEntity.internalServerError().body("Błąd podczas zapisu struktury ankiet: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveSurveyOverviewDTO>> getActiveSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveysForAdmin());
    }

    @GetMapping("/active/by-code/{code}")
    public ResponseEntity<?> getActiveSurveysByCode(@PathVariable String code) {
        try {
            List<ActiveSurveyOverviewDTO> surveys = surveyService.getActiveSurveysByCode(code);
            return ResponseEntity.ok(surveys);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}