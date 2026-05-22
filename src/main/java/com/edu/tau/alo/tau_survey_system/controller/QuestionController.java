package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Question;
import com.edu.tau.alo.tau_survey_system.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public List<Question> getAll(@RequestParam(required = false) Long categoryId) {
        return questionService.getAllQuestions(categoryId);
    }

    @GetMapping("/{id}")
    public Question getOne(@PathVariable String id) {
        return questionService.getAllQuestions(null)
                .stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nie znaleziono"));
    }

    @PostMapping
    public Question create(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable String id, @RequestBody Question question) {
        return questionService.updateQuestion(id, question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}