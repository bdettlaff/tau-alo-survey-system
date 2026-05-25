package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.model.Question;
import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.dto.SurveyBlockDTO;
import com.edu.tau.alo.tau_survey_system.service.ClassService;
import com.edu.tau.alo.tau_survey_system.repository.QuestionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ClassController {

    private final ClassService classService;
    private final QuestionRepository questionRepository;

    @Autowired
    public ClassController(ClassService classService, QuestionRepository questionRepository) {
        this.classService = classService;
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public List<Classes> getAllClasses() {
        return classService.findAllClasses();
    }

    @GetMapping("/{classId}/survey-blocks")
    public List<SurveyBlockDTO> getClassSurveyBlocks(@PathVariable Long classId) {
        List<Question> allQuestions = questionRepository.findAll();

        List<Question> globalTeacherQuestions = allQuestions.stream()
                .filter(q -> q.getCategory() != null && "Ogólna ocena zajęć".equals(q.getCategory().getName()))
                .collect(Collectors.toList());

        List<SurveyBlockDTO> dtos = classService.getAssignmentsByClassId(classId).stream().map((TeacherAssignment assignment) -> {
            String fullName = assignment.getTeacher() != null
                    ? assignment.getTeacher().getFirstName() + " " + assignment.getTeacher().getLastName()
                    : "Nieznany Nauczyciel";

            String subjectName = assignment.getSubject() != null ? assignment.getSubject().getName() : "Brak przedmiotu";
            String moduleType = (assignment.getSubject() != null && assignment.getSubject().getModuleType() != null)
                    ? assignment.getSubject().getModuleType() : "Ogólny";

            List<Question> blockQuestions = new ArrayList<>(globalTeacherQuestions);

            if (assignment.getSubject() != null && assignment.getSubject().getQuestionCategory() != null) {
                Long targetCategoryId = assignment.getSubject().getQuestionCategory().getId();

                List<Question> specificQuestions = allQuestions.stream()
                        .filter(q -> q.getCategory() != null && q.getCategory().getId().equals(targetCategoryId))
                        .collect(Collectors.toList());

                for (Question specQ : specificQuestions) {
                    boolean alreadyExists = blockQuestions.stream().anyMatch(q -> q.getId().equals(specQ.getId()));
                    if (!alreadyExists) {
                        blockQuestions.add(specQ);
                    }
                }
            }

            return new SurveyBlockDTO(
                    assignment.getId(),
                    assignment.getTeacher() != null ? assignment.getTeacher().getId() : null,
                    fullName,
                    assignment.getSubject() != null ? assignment.getSubject().getId() : null,
                    subjectName,
                    moduleType,
                    false,
                    blockQuestions
            );
        }).collect(Collectors.toList());

        List<Question> schoolQuestions = allQuestions.stream()
                .filter(q -> q.getCategory() != null && q.getCategory().getName().contains("Ewaluacja szkoły"))
                .collect(Collectors.toList());

        dtos.add(new SurveyBlockDTO(
                999999L, null, "Ewaluacja Szkoły", null, "Sekcja końcowa", "Ankieta Ogólna", true, schoolQuestions
        ));

        return dtos;
    }
}