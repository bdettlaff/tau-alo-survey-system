package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "module_type")
    private String moduleType;

    // NOWE POLE: Relacja łącząca przedmiot z kategorią pytań w bazie danych
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_category_id", nullable = true)
    private QuestionCategory questionCategory;
}