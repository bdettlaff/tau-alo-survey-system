package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class TeacherApiController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/test")
    public String test() {
        Long count = (Long) em.createQuery("SELECT COUNT(t) FROM Teacher t").getSingleResult();
        return "OK teachers=" + count;
    }


    @GetMapping
    public List<Teacher> teachers() {
        return em.createQuery(
                "SELECT t FROM Teacher t ORDER BY t.lastName, t.firstName",
                Teacher.class
        ).getResultList();
    }
}