package com.edu.tau.alo.tau_survey_system.dto;

public class ActiveSurveyOverviewDTO {
    private Long surveyId;
    private String typeOrTeacher; // Np. "Jan Kowalski (Matematyka)" lub "Ogólnoszkolna"
    private String targetClass;   // Np. "4TA"
    private String startDate;
    private String endDate;

    public ActiveSurveyOverviewDTO(Long surveyId, String typeOrTeacher, String targetClass, String startDate, String endDate) {
        this.surveyId = surveyId;
        this.typeOrTeacher = typeOrTeacher;
        this.targetClass = targetClass;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Gettery i settery
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
}