package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_result_id")
    private SurveyResult surveyResult;

    private String questionCode; // np. "A1", "A2", "B2", "C1"
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String commentType;
}