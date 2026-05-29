package com.edu.tau.alo.tau_survey_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ActiveSurveyOverviewDTO {

    private List<SurveyEntryDTO> surveys;
    private TokenInfoDTO tokenInfo;

    public ActiveSurveyOverviewDTO(List<SurveyEntryDTO> surveys, TokenInfoDTO tokenInfo) {
        this.surveys = surveys;
        this.tokenInfo = tokenInfo;
    }

    public List<SurveyEntryDTO> getSurveys() { return surveys; }
    public void setSurveys(List<SurveyEntryDTO> surveys) { this.surveys = surveys; }

    public TokenInfoDTO getTokenInfo() { return tokenInfo; }
    public void setTokenInfo(TokenInfoDTO tokenInfo) { this.tokenInfo = tokenInfo; }

    // -----------------------------------------------------------------------
    // Zagnieżdżony DTO — pojedyncza ankieta (dawna zawartość tej klasy)
    // -----------------------------------------------------------------------
    public static class SurveyEntryDTO {

        private Long surveyId;
        private String typeOrTeacher;
        private String targetClass;
        private String startDate;
        private String endDate;
        private String accessCode;
        private boolean isSchoolSurvey;

        public SurveyEntryDTO(Long surveyId, String typeOrTeacher, String targetClass,
                              String startDate, String endDate, String accessCode,
                              boolean isSchoolSurvey) {
            this.surveyId = surveyId;
            this.typeOrTeacher = typeOrTeacher;
            this.targetClass = targetClass;
            this.startDate = startDate;
            this.endDate = endDate;
            this.accessCode = accessCode;
            this.isSchoolSurvey = isSchoolSurvey;
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

        @JsonProperty("isSchoolSurvey")
        public boolean isSchoolSurvey() { return isSchoolSurvey; }
        public void setSchoolSurvey(boolean schoolSurvey) { isSchoolSurvey = schoolSurvey; }
    }
}