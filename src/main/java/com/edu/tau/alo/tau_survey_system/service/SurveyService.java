package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.dto.SurveySummaryDTO;
import com.edu.tau.alo.tau_survey_system.model.*;
import com.edu.tau.alo.tau_survey_system.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyResultRepository surveyRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherAssignmentRepository assignmentRepository;

    public SurveyService(SurveyResultRepository surveyRepository, TeacherRepository teacherRepository,
                         SubjectRepository subjectRepository, TeacherAssignmentRepository assignmentRepository) {
        this.surveyRepository = surveyRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
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
        List<SurveyResult> results = surveyRepository.findByTeacherId(teacher.getId());
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