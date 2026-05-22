package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByIsActiveTrue();
    List<Question> findByIsActiveTrueAndCategoryId(Long categoryId);
}