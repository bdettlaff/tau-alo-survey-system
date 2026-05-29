package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.SurveySummaryDTO;
import com.edu.tau.alo.tau_survey_system.model.Teacher;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("@userAuthService.hasRole(authentication, 'ADMIN')")  // ← dotyczy wszystkich metod
public class SurveyResultsController {

    private final SurveyService surveyService;

    @Autowired
    public SurveyResultsController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/all")
    public List<SurveySummaryDTO> getAllResults() {
        return surveyService.getAllTeacherSummaries();
    }

    // Zwraca 204 No Content gdy brak danych szkolnych — frontend obsługuje to jako null
    @GetMapping("/school")
    public ResponseEntity<SurveySummaryDTO> getSchoolResults() {
        SurveySummaryDTO result = surveyService.getSchoolSummary();
        if (result == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/teachers-list")
    public List<Teacher> getAllTeachers() {
        return surveyService.getAllTeachers();
    }

    @GetMapping("/subjects-list")
    public List<String> getAllSubjects() {
        return surveyService.getAllSubjects();
    }
}