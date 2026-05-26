package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.dto.*;
import com.edu.tau.alo.tau_survey_system.model.*;
import com.edu.tau.alo.tau_survey_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyResultRepository surveyResultRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SurveyRepository surveyRepository;
    private final ClassRepository classRepository;
    private final QuestionRepository questionRepository;
    private final TeacherAssignmentRepository assignmentRepository;

    public SurveyService(SurveyResultRepository surveyResultRepository, TeacherRepository teacherRepository,
                         SubjectRepository subjectRepository, SurveyRepository surveyRepository,
                         ClassRepository classRepository, QuestionRepository questionRepository,
                         TeacherAssignmentRepository assignmentRepository) {
        this.surveyResultRepository = surveyResultRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.surveyRepository = surveyRepository;
        this.classRepository = classRepository;
        this.questionRepository = questionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Question> getQuestions(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .map(Survey::getQuestions)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ankiety o ID: " + surveyId));
    }

    @Transactional
    public void saveCompositeSurveyStructure(CompositeSurveyRequest request) {
        Classes schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klasy o ID: " + request.getClassId()));

        LocalDate parsedStartDate = LocalDate.parse(request.getStartDate());
        LocalDate parsedEndDate = LocalDate.parse(request.getEndDate());

        if (request.getTeacherSurveys() != null) {
            for (TeacherSurveySelection selection : request.getTeacherSurveys()) {
                Teacher teacher = teacherRepository.findById(selection.getTeacherId())
                        .orElseThrow(() -> new IllegalArgumentException("Brak nauczyciela ID: " + selection.getTeacherId()));

                Subject subject = subjectRepository.findById(selection.getSubjectId())
                        .orElseThrow(() -> new IllegalArgumentException("Brak przedmiotu ID: " + selection.getSubjectId()));

                List<Question> questions = questionRepository.findAllById(selection.getQuestionIds());

                Survey survey = new Survey();
                survey.setClasses(schoolClass);
                survey.setTeacher(teacher);
                survey.setSubject(subject);
                survey.setQuestions(questions);
                survey.setStartDate(parsedStartDate);
                survey.setEndDate(parsedEndDate);
                survey.setActive(true);
                surveyRepository.save(survey);
            }
        }
    }

    @Transactional
    public void saveAnswers(Long surveyId, Map<String, Object> answers, String studentId) {
        if (surveyResultRepository.existsBySurveyIdAndStudentId(surveyId, studentId)) {
            throw new IllegalStateException("Już wypełniłeś tę ankietę!");
        }

        Survey survey = surveyRepository.findById(surveyId).orElseThrow();

        // --- Rygorystyczne sprawdzanie klasy ---
        if (survey.getClasses() == null || survey.getClasses().getName() == null) {
            throw new IllegalStateException("Błąd: Ankieta nie jest poprawnie przypisana do żadnej klasy!");
        }

        SurveyResult result = new SurveyResult();
        result.setSurvey(survey);
        result.setTeacher(survey.getTeacher());
        result.setSubject(survey.getSubject());
        result.setStudentId(studentId);

        // Zapis klasy
        result.setClassName(survey.getClasses().getName());

        Map<String, Double> scores = new HashMap<>();
        Map<String, String> commentsMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            if (entry.getValue() instanceof Number) {
                scores.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
            } else if (entry.getValue() instanceof String && (entry.getKey().equals("A+") || entry.getKey().equals("A-"))) {
                commentsMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        result.setQuestionScores(scores);
        result.setComments(commentsMap);
        surveyResultRepository.save(result);
    }

    public List<ActiveSurveyOverviewDTO> getActiveSurveysForAdmin() {
        return surveyRepository.findByIsActiveTrue().stream().map(survey -> {
            String target = (survey.getTeacher() != null) ? survey.getTeacher().getFirstName() + " " + survey.getTeacher().getLastName() : "Ogólna";
            return new ActiveSurveyOverviewDTO(survey.getId(), target, survey.getClasses().getName(),
                    survey.getStartDate().toString(), survey.getEndDate().toString());
        }).collect(Collectors.toList());
    }

    public List<Teacher> getAllTeachers() { return teacherRepository.findAll(); }

    public List<String> getAllSubjects() {
        return subjectRepository.findAll().stream().map(Subject::getName).distinct().collect(Collectors.toList());
    }

    public List<SurveySummaryDTO> getAllTeacherSummaries() {
        return teacherRepository.findAll().stream().map(this::getResultsForTeacher).collect(Collectors.toList());
    }

    private SurveySummaryDTO getResultsForTeacher(Teacher teacher) {
        List<SurveyResult> results = surveyResultRepository.findByTeacherId(teacher.getId());
        SurveySummaryDTO dto = new SurveySummaryDTO();
        dto.setTeacherId(teacher.getId());
        dto.setTeacherName(teacher.getFirstName() + " " + teacher.getLastName());
        dto.setTotalVotes((long) results.size());

        // ZBIERANIE UNIKALNYCH KLAS
        Set<String> classes = results.stream()
                .map(SurveyResult::getClassName)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toSet());
        dto.setClassNames(classes);

        if (!results.isEmpty()) {
            Map<String, Double> averages = results.stream()
                    .flatMap(r -> r.getQuestionScores().entrySet().stream())
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));

            Map<String, Double> formattedAverages = new HashMap<>();
            averages.forEach((key, val) -> formattedAverages.put("avg" + key, val));
            dto.setAverages(formattedAverages);

            dto.setComments(results.stream()
                    .flatMap(r -> r.getComments().entrySet().stream())
                    .map(e -> new CommentDTO(e.getValue(), e.getKey().equals("A+") ? "POZYTYWNA" : "KONSTRUKTYWNA"))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}