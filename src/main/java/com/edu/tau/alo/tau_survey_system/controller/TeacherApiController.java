package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Teacher;
import com.edu.tau.alo.tau_survey_system.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class TeacherApiController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherApiController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/test")
    public String test() {
        return "OK teachers=" + teacherService.getTeacherCount();
    }

    @GetMapping
    public List<Teacher> teachers() {
        return teacherService.findAllSorted();
    }
}