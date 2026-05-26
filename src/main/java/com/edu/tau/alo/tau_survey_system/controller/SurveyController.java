package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Answer;
import com.edu.tau.alo.tau_survey_system.model.SurveyResult;
import com.edu.tau.alo.tau_survey_system.repository.AnswerRepository;
import com.edu.tau.alo.tau_survey_system.repository.SurveyResultRepository;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@CrossOrigin(origins = "http://localhost:3000")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private AnswerRepository answerRepository; // Dodano wstrzyknięcie repozytorium odpowiedzi

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getQuestions(id));
    }

    @GetMapping("/results")
    public ResponseEntity<List<SurveyResult>> getAllResults() {
        return ResponseEntity.ok(surveyResultRepository.findAll());
    }

    // Nowy endpoint do debugowania surowych danych
    @GetMapping("/debug/answers/{teacherId}")
    public ResponseEntity<List<Answer>> getRawAnswers(@PathVariable Long teacherId) {
        return ResponseEntity.ok(answerRepository.findBySurveyResult_Teacher_Id(teacherId));
    }

    @GetMapping("/{id}/is-completed")
    public ResponseEntity<Boolean> isSurveyCompleted(@PathVariable Long id, Principal principal) {
        String studentId = principal.getName();
        boolean completed = surveyResultRepository.existsBySurveyIdAndStudentId(id, studentId);
        return ResponseEntity.ok(completed);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitSurvey(@PathVariable Long id,
                                          @RequestBody Map<String, Object> answers,
                                          Principal principal) {
        try {
            String studentId = principal.getName();
            surveyService.saveAnswers(id, answers, studentId);
            return ResponseEntity.ok("Zapisano");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd serwera: " + e.getMessage());
        }
    }
}