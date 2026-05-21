package com.edu.tau.alo.tau_survey_system.repository;

import com.edu.tau.alo.tau_survey_system.model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {
}