package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SurveySummaryDTO {
    private Long teacherId;
    private String teacherName;
    private String subjectName;
    private long totalVotes;
    private Map<String, Long> totalVotesPerClass; // Nowe pole
    private Map<String, Double> averages;
    private Map<String, Map<String, Double>> averagesPerClass;
    private List<CommentDTO> comments;
    private Map<String, List<CommentDTO>> commentsPerClass;
    private Set<String> classNames;

    // Konstruktor domyślny
    public SurveySummaryDTO() {
    }

    // Konstruktor pełny
    public SurveySummaryDTO(Long teacherId, String teacherName, String subjectName, long totalVotes,
                            Map<String, Long> totalVotesPerClass, Map<String, Double> averages,
                            Map<String, Map<String, Double>> averagesPerClass,
                            List<CommentDTO> comments, Map<String, List<CommentDTO>> commentsPerClass,
                            Set<String> classNames) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.totalVotes = totalVotes;
        this.totalVotesPerClass = totalVotesPerClass;
        this.averages = averages;
        this.averagesPerClass = averagesPerClass;
        this.comments = comments;
        this.commentsPerClass = commentsPerClass;
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

    public Map<String, Long> getTotalVotesPerClass() {
        return totalVotesPerClass;
    }

    public void setTotalVotesPerClass(Map<String, Long> totalVotesPerClass) {
        this.totalVotesPerClass = totalVotesPerClass;
    }

    public Map<String, Double> getAverages() {
        return averages;
    }

    public void setAverages(Map<String, Double> averages) {
        this.averages = averages;
    }

    public Map<String, Map<String, Double>> getAveragesPerClass() {
        return averagesPerClass;
    }

    public void setAveragesPerClass(Map<String, Map<String, Double>> averagesPerClass) {
        this.averagesPerClass = averagesPerClass;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Map<String, List<CommentDTO>> getCommentsPerClass() {
        return commentsPerClass;
    }

    public void setCommentsPerClass(Map<String, List<CommentDTO>> commentsPerClass) {
        this.commentsPerClass = commentsPerClass;
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(Set<String> classNames) {
        this.classNames = classNames;
    }
}