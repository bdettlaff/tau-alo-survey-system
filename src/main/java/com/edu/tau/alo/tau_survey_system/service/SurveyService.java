package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.dto.CompositeSurveyRequest;
import com.edu.tau.alo.tau_survey_system.dto.TeacherSurveySelection;
import com.edu.tau.alo.tau_survey_system.dto.SurveySummaryDTO;
import com.edu.tau.alo.tau_survey_system.model.*;
import com.edu.tau.alo.tau_survey_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyResultRepository surveyResultRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final SurveyRepository surveyRepository;
    private final ClassRepository classRepository;
    private final QuestionRepository questionRepository;

    public SurveyService(SurveyResultRepository surveyResultRepository, TeacherRepository teacherRepository,
                         SubjectRepository subjectRepository, TeacherAssignmentRepository assignmentRepository,
                         SurveyRepository surveyRepository, ClassRepository classRepository,
                         QuestionRepository questionRepository) {
        this.surveyResultRepository = surveyResultRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
        this.surveyRepository = surveyRepository;
        this.classRepository = classRepository;
        this.questionRepository = questionRepository;
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
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono nauczyciela o ID: " + selection.getTeacherId()));

                Subject subject = subjectRepository.findById(selection.getSubjectId())
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono przedmiotu o ID: " + selection.getSubjectId()));

                List<Question> questions = questionRepository.findAllById(selection.getQuestionIds());

                if (questions.isEmpty()) {
                    continue;
                }

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


        if (request.getSchoolQuestionIds() != null && !request.getSchoolQuestionIds().isEmpty()) {
            List<Question> schoolQuestions = questionRepository.findAllById(request.getSchoolQuestionIds());

            if (!schoolQuestions.isEmpty()) {
                Survey schoolSurvey = new Survey();
                schoolSurvey.setClasses(schoolClass);
                schoolSurvey.setTeacher(null);
                schoolSurvey.setSubject(null);
                schoolSurvey.setQuestions(schoolQuestions);
                schoolSurvey.setStartDate(parsedStartDate);
                schoolSurvey.setEndDate(parsedEndDate);
                schoolSurvey.setActive(true);

                surveyRepository.save(schoolSurvey);
            }
        }

        System.out.println("=== ZAPISANO STRUKTURĘ ANKIET W BAZIE DANYCH ===");
    }

    @Transactional
    public void generateCompositeSurveys(CompositeSurveyRequest request) {
        saveCompositeSurveyStructure(request);
    }

    public List<SurveySummaryDTO> getAllTeacherSummaries() {
        return teacherRepository.findAll().stream()
                .map(this::getResultsForTeacher)
                .collect(Collectors.toList());
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<String> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(Subject::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    private SurveySummaryDTO getResultsForTeacher(Teacher teacher) {
        List<SurveyResult> results = surveyResultRepository.findByTeacherId(teacher.getId());
        SurveySummaryDTO dto = new SurveySummaryDTO();
        dto.setTeacherId(teacher.getId());
        dto.setTeacherName(teacher.getFirstName() + " " + teacher.getLastName());

        String subjectName = assignmentRepository.findAll().stream()
                .filter(ta -> ta.getTeacher() != null && ta.getTeacher().getId().equals(teacher.getId()))
                .map(ta -> ta.getSubject().getName())
                .findFirst()
                .orElse("Brak przedmiotu");

        dto.setSubjectName(subjectName);
        dto.setTotalVotes((long) results.size());

        if (!results.isEmpty()) {
            dto.setAvgClarity(results.stream().mapToDouble(SurveyResult::getScoreClarity).average().orElse(0.0));
            dto.setAvgPreparation(results.stream().mapToDouble(SurveyResult::getScorePreparation).average().orElse(0.0));
            dto.setAvgFairness(results.stream().mapToDouble(SurveyResult::getScoreFairness).average().orElse(0.0));
            dto.setAvgCulture(results.stream().mapToDouble(SurveyResult::getScoreCulture).average().orElse(0.0));
            dto.setComments(results.stream()
                    .map(r -> new SurveySummaryDTO.CommentDTO(r.getStudentComment(), r.getCommentType()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}