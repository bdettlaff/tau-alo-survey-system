package com.edu.tau.alo.tau_survey_system.service;

import com.edu.tau.alo.tau_survey_system.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeacherService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public Long getTeacherCount() {
        return em.createQuery("SELECT COUNT(t) FROM Teacher t", Long.class)
                .getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAllSorted() {
        return em.createQuery(
                "SELECT t FROM Teacher t ORDER BY t.lastName, t.firstName",
                Teacher.class
        ).getResultList();
    }
}