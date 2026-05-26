package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private Teacher teacher;
    @ManyToOne private Subject subject;
    @ManyToOne private Survey survey;

    private String studentId;
    private String className;

    // --- DYNAMICZNY SYSTEM OCEN ---
    @ElementCollection
    @CollectionTable(name = "survey_question_scores", joinColumns = @JoinColumn(name = "survey_result_id"))
    @MapKeyColumn(name = "question_key")
    @Column(name = "score")
    private Map<String, Double> questionScores = new HashMap<>();

    // --- NOWY SYSTEM KOMENTARZY ---
    @ElementCollection
    @CollectionTable(name = "survey_comments", joinColumns = @JoinColumn(name = "survey_result_id"))
    @MapKeyColumn(name = "comment_key") // Przechowuje "A+" lub "A-"
    @Column(name = "comment_text")      // Przechowuje treść komentarza
    private Map<String, String> comments = new HashMap<>();
}