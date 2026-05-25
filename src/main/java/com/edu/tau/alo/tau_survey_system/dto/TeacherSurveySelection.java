package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;
import lombok.Data;

@Data
public class TeacherSurveySelection {
    private Long teacherId;
    private Long subjectId;
    private List<String> questionIds;
}