package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "classes")
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true) // lub false, jeśli każda klasa musi mieć slug
    private String slug;
}