package com.edu.tau.alo.tau_survey_system.dto;

import java.util.Map;
import java.util.List;

public class SurveySummaryDTO {
    private Long teacherId;
    private String teacherName;
    private String subjectName;
    private long totalVotes;
    private Map<String, Double> averages;
    private List<CommentDTO> comments;

    // Konstruktor domyślny
    public SurveySummaryDTO() {
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

    // Klasa pomocnicza dla komentarzy
    public static class CommentDTO {
        private String text;
        private String type;

        public CommentDTO(String text, String type) {
            this.text = text;
            this.type = type;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}