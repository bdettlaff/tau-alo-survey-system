package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.model.Question;
import com.edu.tau.alo.tau_survey_system.model.TeacherAssignment;
import com.edu.tau.alo.tau_survey_system.dto.SurveyBlockDTO;
import com.edu.tau.alo.tau_survey_system.service.ClassService;
import com.edu.tau.alo.tau_survey_system.repository.QuestionRepository;
import com.edu.tau.alo.tau_survey_system.repository.ClassRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ClassController {

    private final ClassService classService;
    private final QuestionRepository questionRepository;
    private final ClassRepository classRepository;

    @Autowired
    public ClassController(ClassService classService, QuestionRepository questionRepository,
                           ClassRepository classRepository) {
        this.classService = classService;
        this.questionRepository = questionRepository;
        this.classRepository = classRepository;
    }

    @GetMapping
    public List<Classes> getAllClasses() {
        return classService.findAllClasses();
    }

    // Zwraca listę nazw przedmiotów dla klasy na podstawie kodu dostępu
    // Używane przez ankietę szkolną (B3) do wyświetlenia listy przedmiotów
    @GetMapping("/subjects-by-code/{code}")
    public ResponseEntity<List<String>> getSubjectsByCode(@PathVariable String code) {
        return classRepository.findByAccessCode(code.toUpperCase())
                .map(schoolClass -> {
                    List<String> subjects = classService.getAssignmentsByClassId(schoolClass.getId())
                            .stream()
                            .filter(a -> a.getSubject() != null)
                            .map(a -> a.getSubject().getName())
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(subjects);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{classId}/survey-blocks")
    public List<SurveyBlockDTO> getClassSurveyBlocks(@PathVariable Long classId) {
        List<Question> allQuestions = questionRepository.findAll();
        Map<String, Question> byId = allQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        String className = classRepository.findById(classId)
                .map(Classes::getName)
                .orElse("");

        List<String> generalIds = List.of("A1","A2","A3","A4","A5","A6","A7","A8","A9","A+","A-");

        List<SurveyBlockDTO> dtos = classService.getAssignmentsByClassId(classId).stream()
                .map((TeacherAssignment assignment) -> {
                    String fullName = assignment.getTeacher() != null
                            ? assignment.getTeacher().getFirstName() + " " + assignment.getTeacher().getLastName()
                            : "Nieznany Nauczyciel";
                    String subjectName = assignment.getSubject() != null
                            ? assignment.getSubject().getName() : "Brak przedmiotu";
                    String moduleType = (assignment.getSubject() != null
                            && assignment.getSubject().getModuleType() != null)
                            ? assignment.getSubject().getModuleType() : "Ogólny";

                    List<String> questionIds = new ArrayList<>(generalIds);

                    if (assignment.getSubject() != null) {
                        String name = assignment.getSubject().getName().toLowerCase();
                        String module = moduleType.toLowerCase();
                        boolean isALO = className.contains("ALO");
                        boolean isClass3 = className.startsWith("3");
                        boolean isClass5 = className.startsWith("5");

                        if (isJezykObcy(name)) addIfAbsent(questionIds, List.of("L1", "L4"));
                        if (isScisPelny(name, isALO)) addIfAbsent(questionIds, List.of("S2", "S3"));
                        else if (isScisSkrocony(name)) addIfAbsent(questionIds, List.of("S1"));
                        if (isPolski(name)) addIfAbsent(questionIds, List.of("P1", "P2", "P3"));
                        if (isWF(name)) addIfAbsent(questionIds, List.of("W2", "W3"));

                        if (isZawodowe(module)) {
                            addIfAbsent(questionIds, List.of("Z1", "Z2"));
                            if (isTP(module, name)) addIfAbsent(questionIds, List.of("ZP3", "ZP4"));
                            if (isTL(module, name)) addIfAbsent(questionIds, List.of("ZL3", "ZL4"));
                            if (isClass3) addIfAbsent(questionIds, List.of("Z5a"));
                            if (isClass5) addIfAbsent(questionIds, List.of("Z5b"));
                        }
                    }

                    List<Question> blockQuestions = questionIds.stream()
                            .map(byId::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

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

        List<Question> schoolQuestions = List.of("B1","B2","B3","B+").stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dtos.add(new SurveyBlockDTO(
                999999L, null, "Ewaluacja Szkoły", null,
                "Sekcja końcowa", "Ankieta Ogólna", true, schoolQuestions
        ));

        return dtos;
    }

    private boolean isJezykObcy(String name) {
        return name.contains("angielski") || name.contains("niemiecki")
                || name.contains("rosyjski") || name.contains("angielski zawodowy")
                || name.contains("angielski w logistyce") || name.contains("angielski w transporcie");
    }

    private boolean isPolski(String name) {
        return name.contains("język polski") || name.contains("polski dla obcokrajowców")
                || name.contains("j.polskiego");
    }

    private boolean isWF(String name) {
        return name.contains("wychowanie fizyczne") || name.equals("sks");
    }

    private boolean isScisPelny(String name, boolean isALO) {
        if (name.equals("matematyka")) return true;
        if (name.equals("biologia") && isALO) return true;
        return false;
    }

    private boolean isScisSkrocony(String name) {
        return name.equals("fizyka") || name.equals("chemia")
                || name.equals("biologia")
                || name.equals("informatyka");
    }

    private boolean isZawodowe(String module) {
        return module.contains("zawodow") || module.contains("logistyk")
                || module.contains("it)") || module.contains("it");
    }

    private boolean isTP(String module, String name) {
        return module.contains("it") || name.contains("programow")
                || name.contains("aplikacj") || name.contains("baz danych")
                || name.contains("witryn") || name.contains("webow")
                || name.contains("informatyki");
    }

    private boolean isTL(String module, String name) {
        return module.contains("logistyk") || name.contains("logistyk")
                || name.contains("magazyn") || name.contains("transport")
                || name.contains("spedycj");
    }

    private void addIfAbsent(List<String> list, List<String> toAdd) {
        for (String id : toAdd) {
            if (!list.contains(id)) list.add(id);
        }
    }
}