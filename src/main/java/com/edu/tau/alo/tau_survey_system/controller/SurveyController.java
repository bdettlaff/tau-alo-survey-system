package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.TeacherSurveyGroupDTO;
import com.edu.tau.alo.tau_survey_system.model.Answer;
import com.edu.tau.alo.tau_survey_system.model.SurveyResult;
import com.edu.tau.alo.tau_survey_system.repository.AnswerRepository;
import com.edu.tau.alo.tau_survey_system.repository.SurveyResultRepository;
import com.edu.tau.alo.tau_survey_system.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surveys")
@CrossOrigin(origins = "https://apisurveys.vercel.app")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private AnswerRepository answerRepository;

    // ── DEBUG — sprawdź co jest w bazie ──────────────────────────────────────
    @GetMapping("/debug/all")
    public ResponseEntity<?> debugAllSurveys() {
        return ResponseEntity.ok(
                surveyService.getActiveSurveysForAdmin().stream()
                        .map(s -> java.util.Map.of(
                                "surveyId", s.getSurveyId(),
                                "typeOrTeacher", s.getTypeOrTeacher(),
                                "targetClass", s.getTargetClass(),
                                "isSchoolSurvey", s.isSchoolSurvey(),
                                "accessCode", s.getAccessCode() != null ? s.getAccessCode() : "null"
                        ))
                        .collect(java.util.stream.Collectors.toList())
        );
    }

    // ── istniejące endpointy ──────────────────────────────────────────────────

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getQuestions(id));
    }

    @GetMapping("/results")
    public ResponseEntity<List<SurveyResult>> getAllResults() {
        return ResponseEntity.ok(surveyResultRepository.findAll());
    }

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

    // ── endpointy dla zgrupowanych ankiet ─────────────────────────────────────

    /**
     * Zwraca zgrupowane ankiety na podstawie listy surveyIds.
     * GET /api/surveys/group/{firstId}?ids=1,2,3&code=KOD
     * {firstId} jest ignorowany — dane budowane są z ids
     */
    @GetMapping("/group/{firstId}")
    public ResponseEntity<?> getGroupedSurveys(@PathVariable Long firstId,
                                               @RequestParam String ids,
                                               @RequestParam String code) {
        try {
            List<Long> surveyIds = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            TeacherSurveyGroupDTO group = surveyService.getGroupedSurveysByIds(surveyIds, code);
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd serwera: " + e.getMessage());
        }
    }

    /**
     * Sprawdza czy student wypełnił wszystkie ankiety z grupy.
     * GET /api/surveys/group/{firstId}/is-completed?ids=1,2,3
     */
    @GetMapping("/group/{firstId}/is-completed")
    public ResponseEntity<Boolean> isGroupCompleted(@PathVariable Long firstId,
                                                    @RequestParam String ids,
                                                    Principal principal) {
        try {
            String studentId = principal.getName();
            List<Long> surveyIds = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            boolean completed = surveyService.isGroupCompletedByIds(surveyIds, studentId);
            return ResponseEntity.ok(completed);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Zapisuje odpowiedzi dla wszystkich ankiet nauczyciela naraz.
     * Body: { surveyId1: { "Q1": 8, "A+": "..." }, surveyId2: { ... } }
     * POST /api/surveys/group/submit
     */
    @PostMapping("/group/submit")
    public ResponseEntity<?> submitGroupSurvey(@RequestBody Map<Long, Map<String, Object>> answers,
                                               Principal principal) {
        try {
            String studentId = principal.getName();
            surveyService.saveGroupAnswers(answers, studentId);
            return ResponseEntity.ok("Zapisano");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd serwera: " + e.getMessage());
        }
    }
}