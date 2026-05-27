package com.edu.tau.alo.tau_survey_system.dto;

public class ActiveSurveyOverviewDTO {
    private Long surveyId;
    private String typeOrTeacher;
    private String targetClass;
    private String startDate;
    private String endDate;
    private String accessCode; // NOWE

    public ActiveSurveyOverviewDTO(Long surveyId, String typeOrTeacher, String targetClass,
                                   String startDate, String endDate, String accessCode) {
        this.surveyId = surveyId;
        this.typeOrTeacher = typeOrTeacher;
        this.targetClass = targetClass;
        this.startDate = startDate;
        this.endDate = endDate;
        this.accessCode = accessCode;
    }

    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

    public String getTypeOrTeacher() { return typeOrTeacher; }
    public void setTypeOrTeacher(String typeOrTeacher) { this.typeOrTeacher = typeOrTeacher; }

    public String getTargetClass() { return targetClass; }
    public void setTargetClass(String targetClass) { this.targetClass = targetClass; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }
}
