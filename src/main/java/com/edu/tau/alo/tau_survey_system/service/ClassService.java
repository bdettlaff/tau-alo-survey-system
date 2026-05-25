package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import com.edu.tau.alo.tau_survey_system.repository.TeacherAssignmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;

    public ClassService(ClassRepository classRepository, TeacherAssignmentRepository teacherAssignmentRepository) {
        this.classRepository = classRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
    }

    public List<Classes> findAllClasses() {
        return classRepository.findAll();
    }

    public List<TeacherAssignment> getAssignmentsByClassId(Long classId) {
        return teacherAssignmentRepository.findByClazzId(classId);
    }

    public Classes findClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono klasy o ID: " + id));
    }
}