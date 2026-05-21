package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.repository.TeacherAssignmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class AssignmentController {

    private final TeacherAssignmentRepository assignmentRepository;

    public AssignmentController(TeacherAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    // Endpoint: /api/classes/{id}/blocks
    // Pobiera przypisania nauczycieli dla konkretnej klasy o podanym ID
    @GetMapping("/{classId}/blocks")
    public List<TeacherAssignment> getBlocksByClass(@PathVariable Long classId) {
        return assignmentRepository.findByClazzId(classId);
    }
}