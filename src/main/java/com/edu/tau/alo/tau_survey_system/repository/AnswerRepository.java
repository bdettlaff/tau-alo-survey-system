package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Metoda pobierająca odpowiedzi dla konkretnego nauczyciela
    List<Answer> findBySurveyResult_Teacher_Id(Long teacherId);
}