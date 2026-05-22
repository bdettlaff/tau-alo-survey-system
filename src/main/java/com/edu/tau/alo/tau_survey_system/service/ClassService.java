package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<Classes> findAllClasses() {
        return classRepository.findAll();
    }
}