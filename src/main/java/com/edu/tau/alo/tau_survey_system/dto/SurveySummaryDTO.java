package com.edu.tau.alo.tau_survey_system.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SurveySummaryDTO {
    private Long teacherId;
    private String teacherName;
    private String subjectName;
    private long totalVotes;
    private Map<String, Long> totalVotesPerClass;
    private Map<String, Double> averages;
    private Map<String, Map<String, Double>> averagesPerClass;
    private List<CommentDTO> comments;
    private Map<String, List<CommentDTO>> commentsPerClass;
    private Set<String> classNames;
    private Map<String, String> questionTexts; // NOWE: "A1" -> "Jak ogólnie..."

    public SurveySummaryDTO() {}

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public long getTotalVotes() { return totalVotes; }
    public void setTotalVotes(long totalVotes) { this.totalVotes = totalVotes; }

    public Map<String, Long> getTotalVotesPerClass() { return totalVotesPerClass; }
    public void setTotalVotesPerClass(Map<String, Long> totalVotesPerClass) { this.totalVotesPerClass = totalVotesPerClass; }

    public Map<String, Double> getAverages() { return averages; }
    public void setAverages(Map<String, Double> averages) { this.averages = averages; }

    public Map<String, Map<String, Double>> getAveragesPerClass() { return averagesPerClass; }
    public void setAveragesPerClass(Map<String, Map<String, Double>> averagesPerClass) { this.averagesPerClass = averagesPerClass; }

    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }

    public Map<String, List<CommentDTO>> getCommentsPerClass() { return commentsPerClass; }
    public void setCommentsPerClass(Map<String, List<CommentDTO>> commentsPerClass) { this.commentsPerClass = commentsPerClass; }

    public Set<String> getClassNames() { return classNames; }
    public void setClassNames(Set<String> classNames) { this.classNames = classNames; }

    public Map<String, String> getQuestionTexts() { return questionTexts; }
    public void setQuestionTexts(Map<String, String> questionTexts) { this.questionTexts = questionTexts; }
}
