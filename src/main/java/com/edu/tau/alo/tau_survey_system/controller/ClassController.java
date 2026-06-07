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

        String className = classRepository.findById(classId)
                .map(Classes::getName)
                .orElse("");

        // Wszystkie aktywne pytania indeksowane po id
        Map<String, Question> allById = questionRepository.findByIsActiveTrue().stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        // Pytania ogólne (A1-A9, A+, A-) — zawsze w każdym bloku
        List<String> generalIds = List.of("A1","A2","A3","A4","A5","A6","A7","A8","A9","A+","A-");

        boolean isALO    = className.contains("ALO");
        boolean isClass3 = className.matches("^3.*");
        boolean isClass5 = className.matches("^5.*");

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

                    // Zacznij od pytań ogólnych A
                    Map<String, Question> blockMap = new LinkedHashMap<>();
                    addQuestions(blockMap, generalIds, allById);

                    // Dodaj pytania specjalistyczne wg logiki z dokumentacji
                    if (assignment.getSubject() != null) {
                        String name   = assignment.getSubject().getName().toLowerCase();
                        String module = moduleType.toLowerCase();

                        // Języki obce: L1, L4
                        if (isJezykObcy(name))
                            addQuestions(blockMap, List.of("L1","L4"), allById);

                        // Ścisłe pełne (mat, bio-ALO): S2, S3
                        if (isScisPelny(name, isALO))
                            addQuestions(blockMap, List.of("S2","S3"), allById);
                            // Ścisłe skrócone (fiz, chem, bio-technikum, inf): S1
                        else if (isScisSkrocony(name))
                            addQuestions(blockMap, List.of("S1"), allById);

                        // Język polski: P1, P2, P3
                        if (isPolski(name))
                            addQuestions(blockMap, List.of("P1","P2","P3"), allById);

                        // WF: W2, W3
                        if (isWF(name))
                            addQuestions(blockMap, List.of("W2","W3"), allById);

                        // Zawodowe wspólne: Z1, Z2
                        if (isZawodowe(module)) {
                            addQuestions(blockMap, List.of("Z1","Z2"), allById);

                            // Technik programista: ZP3, ZP4
                            if (isTP(module, name))
                                addQuestions(blockMap, List.of("ZP3","ZP4"), allById);

                            // Technik logistyk: ZL3, ZL4
                            if (isTL(module, name))
                                addQuestions(blockMap, List.of("ZL3","ZL4"), allById);

                            // Klasa 3: Z5a (egzamin w tym roku)
                            if (isClass3)
                                addQuestions(blockMap, List.of("Z5a"), allById);

                            // Klasa 5: Z5b (oba egzaminy)
                            if (isClass5)
                                addQuestions(blockMap, List.of("Z5b"), allById);
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
                            new ArrayList<>(blockMap.values())
                    );
                }).collect(Collectors.toList());

        // Blok szkolny B — zawsze na końcu
        List<Question> schoolQuestions = List.of("B1","B2","B3","B+").stream()
                .map(allById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dtos.add(new SurveyBlockDTO(
                999999L, null, "Ewaluacja Szkoły", null,
                "Sekcja końcowa", "Ankieta Ogólna", true, schoolQuestions
        ));

        return dtos;
    }

    private void addQuestions(Map<String, Question> map, List<String> ids, Map<String, Question> allById) {
        for (String id : ids) {
            if (!map.containsKey(id)) {
                Question q = allById.get(id);
                if (q != null) map.put(id, q);
            }
        }
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
}