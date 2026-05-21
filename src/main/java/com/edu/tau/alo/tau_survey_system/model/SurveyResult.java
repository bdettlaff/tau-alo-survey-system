package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    private Subject subject;

    // Oceny od 1 do 5
    private Double scoreClarity;      // Kod A1
    private Double scorePreparation;  // Kod L4
    private Double scoreFairness;     // Kod B2
    private Double scoreCulture;      // Kod C1

    private String studentComment;
    private String commentType;       // "POZYTYWNA", "KONSTRUKTYWNA"
}