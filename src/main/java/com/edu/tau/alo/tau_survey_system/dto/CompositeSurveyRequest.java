package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;
import lombok.Data;

@Data
public class CompositeSurveyRequest {
    private Long classId;
    private String startDate;
    private String endDate;
    private List<TeacherSurveySelection> teacherSurveys;
    private List<String> schoolQuestionIds;
}