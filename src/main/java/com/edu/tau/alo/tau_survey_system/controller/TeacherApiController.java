package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import com.edu.tau.alo.tau_survey_system.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
// Dodajemy adnotację CrossOrigin, aby Next.js (port 3000) mógł bez przeszkód odpytywać Springa (port 8080)
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class TeacherApiController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/test")
    public String test() {
        Long count = (Long) em.createQuery("SELECT COUNT(t) FROM Teacher t").getSingleResult();
        return "OK teachers=" + count;
    }

    @GetMapping("/teachers")
    public List<Teacher> teachers() {
        return em.createQuery(
                "SELECT t FROM Teacher t ORDER BY t.lastName, t.firstName",
                Teacher.class
        ).getResultList();
    }

    @GetMapping("/classes")
    public List<Classes> classes() {
        return em.createQuery(
                "SELECT c FROM Classes c ORDER BY c.name",
                Classes.class
        ).getResultList();
    }

    @GetMapping("/classes/{classId}/teachers")
    public List<Teacher> teachersForClass(@PathVariable Long classId) {
        // Zabezpieczenie: Upewnij się, czy w encji TeacherAssignment pole powiązane z klasą
        // nazywa się dokładnie 'clazz'. Jeśli nazywa się 'classes' lub 'schoolClass', zmień ta.clazz poniżej.
        return em.createQuery("""
            SELECT DISTINCT t
            FROM TeacherAssignment ta
            JOIN ta.teacher t
            WHERE ta.clazz.id = :classId
            ORDER BY t.lastName, t.firstName
            """, Teacher.class)
                .setParameter("classId", classId)
                .getResultList();
    }
}