package com.edu.tau.alo.tau_survey_system.dto;

import com.edu.tau.alo.tau_survey_system.model.Question;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SurveyBlockDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private String module;
    private boolean isSchoolSection;
    private List<Question> questions;

    public SurveyBlockDTO(Long id, Long teacherId, String teacherName, Long subjectId, String subjectName, String module, boolean isSchoolSection, List<Question> questions) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.module = module;
        this.isSchoolSection = isSchoolSection;
        this.questions = questions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    // @JsonProperty wymusza nazwę "isSchoolSection" zamiast "schoolSection"
    @JsonProperty("isSchoolSection")
    public boolean isSchoolSection() { return isSchoolSection; }
    public void setSchoolSection(boolean schoolSection) { this.isSchoolSection = schoolSection; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}