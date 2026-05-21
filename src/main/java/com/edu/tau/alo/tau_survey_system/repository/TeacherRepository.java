package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

}