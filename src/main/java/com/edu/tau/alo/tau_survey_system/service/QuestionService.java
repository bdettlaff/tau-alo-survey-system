package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.Question;
import com.edu.tau.alo.tau_survey_system.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuestions(Long categoryId) {
        if (categoryId != null) {
            return questionRepository.findByIsActiveTrueAndCategoryId(categoryId);
        }
        return questionRepository.findByIsActiveTrue();
    }

    public Question addQuestion(Question question) {
        question.setIsActive(true);
        return questionRepository.save(question);
    }

    public Question updateQuestion(String id, Question updated) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pytanie nie znalezione"));
        existing.setContent(updated.getContent());
        existing.setType(updated.getType());
        existing.setModule(updated.getModule());
        existing.setClassRequirement(updated.getClassRequirement());
        if (updated.getCategory() != null) {
            existing.setCategory(updated.getCategory());
        }
        return questionRepository.save(existing);
    }

    public void deleteQuestion(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pytanie nie znalezione"));
        question.setIsActive(false);
        questionRepository.save(question);
    }
}