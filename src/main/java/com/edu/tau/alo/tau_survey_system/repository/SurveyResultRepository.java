package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    List<SurveyResult> findByTeacherId(Long teacherId);

    boolean existsBySurveyIdAndStudentId(Long surveyId, String studentId);

    // Dodana metoda do zliczania ankiet dla nauczyciela
    long countByTeacherId(Long teacherId);
}