package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.QuestionCategory;
import com.edu.tau.alo.tau_survey_system.repository.QuestionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private QuestionCategoryRepository categoryRepository;

    public List<QuestionCategory> getAll() {
        return categoryRepository.findAll();
    }

    public QuestionCategory create(QuestionCategory category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}