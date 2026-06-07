package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.service.TeacherAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "https://apisurveys.vercel.app/", allowedHeaders = "*")
public class AssignmentController {

    private final TeacherAssignmentService assignmentService;

    public AssignmentController(TeacherAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/{classId}/blocks")
    public List<TeacherAssignment> getBlocksByClass(@PathVariable Long classId) {
        return assignmentService.getBlocksByClassId(classId);
    }
}