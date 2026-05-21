package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*") // Umożliwia łączenie się z Reactem
public class ClassController {

    private final ClassRepository classRepository;

    public ClassController(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @GetMapping
    public List<Classes> getAllClasses() {
        return classRepository.findAll();
    }
}