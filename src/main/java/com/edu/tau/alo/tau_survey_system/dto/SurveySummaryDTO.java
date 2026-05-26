package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SurveySummaryDTO {
    private Long teacherId;
    private String teacherName;
    private String subjectName;
    private long totalVotes;
    private Map<String, Double> averages;
    private List<CommentDTO> comments;
    private Set<String> classNames; // Nowe pole

    // Konstruktor domyślny
    public SurveySummaryDTO() {
    }

    // Konstruktor pełny
    public SurveySummaryDTO(Long teacherId, String teacherName, String subjectName, long totalVotes,
                            Map<String, Double> averages, List<CommentDTO> comments, Set<String> classNames) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.totalVotes = totalVotes;
        this.averages = averages;
        this.comments = comments;
        this.classNames = classNames;
    }

    // Gettery i Settery
    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Map<String, Double> getAverages() {
        return averages;
    }

    public void setAverages(Map<String, Double> averages) {
        this.averages = averages;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(Set<String> classNames) {
        this.classNames = classNames;
    }
}