package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.repository.TeacherAssignmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;

    public TeacherAssignmentService(TeacherAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<TeacherAssignment> getBlocksByClassId(Long classId) {
        return assignmentRepository.findByClazzId(classId);
    }
}