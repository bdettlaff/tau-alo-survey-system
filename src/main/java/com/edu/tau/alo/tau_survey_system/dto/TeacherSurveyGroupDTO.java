package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;

public class TeacherSurveyGroupDTO {

    private Long teacherId;
    private String teacherName;
    private String targetClass;
    private String startDate;
    private String endDate;
    private List<SurveySection> sections;

    public TeacherSurveyGroupDTO() {}

    public TeacherSurveyGroupDTO(Long teacherId, String teacherName, String targetClass,
                                 String startDate, String endDate, List<SurveySection> sections) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.targetClass = targetClass;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sections = sections;
    }

    public static class SurveySection {
        private Long surveyId;
        private String subjectName;
        private List<QuestionDTO> questions;
        private boolean isSchoolSurvey;

        public SurveySection() {}

        // Konstruktor nauczycielski (bez flagi — domyślnie false)
        public SurveySection(Long surveyId, String subjectName, List<QuestionDTO> questions) {
            this.surveyId = surveyId;
            this.subjectName = subjectName;
            this.questions = questions;
            this.isSchoolSurvey = false;
        }

        // Konstruktor z flagą
        public SurveySection(Long surveyId, String subjectName, List<QuestionDTO> questions, boolean isSchoolSurvey) {
            this.surveyId = surveyId;
            this.subjectName = subjectName;
            this.questions = questions;
            this.isSchoolSurvey = isSchoolSurvey;
        }

        public Long getSurveyId() { return surveyId; }
        public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

        public List<QuestionDTO> getQuestions() { return questions; }
        public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }

        public boolean isSchoolSurvey() { return isSchoolSurvey; }
        public void setSchoolSurvey(boolean schoolSurvey) { isSchoolSurvey = schoolSurvey; }
    }

    public static class QuestionDTO {
        private String id;
        private String content;

        public QuestionDTO() {}

        public QuestionDTO(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTargetClass() { return targetClass; }
    public void setTargetClass(String targetClass) { this.targetClass = targetClass; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public List<SurveySection> getSections() { return sections; }
    public void setSections(List<SurveySection> sections) { this.sections = sections; }
}