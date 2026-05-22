package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.SurveySummaryDTO;
import com.edu.tau.alo.tau_survey_system.model.Teacher;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/teachers-list")
    public List<Teacher> getAllTeachers() {
        return surveyService.getAllTeachers();
    }

    @GetMapping("/subjects-list")
    public List<String> getAllSubjects() {
        return surveyService.getAllSubjects();
    }
}