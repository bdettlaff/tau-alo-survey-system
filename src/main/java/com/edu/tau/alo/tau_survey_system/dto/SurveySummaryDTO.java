package com.edu.tau.alo.tau_survey_system.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SurveySummaryDTO {
    private Long teacherId;
    private String teacherName;
    private String subjectName = "Brak";
    private Long totalVotes = 0L;
    private Double avgClarity = 0.0;
    private Double avgPreparation = 0.0;
    private Double avgFairness = 0.0;
    private Double avgCulture = 0.0;
    private List<CommentDTO> comments = new ArrayList<>();

    @Getter
    @Setter
    public static class CommentDTO {
        private String text;
        private String type;

        public CommentDTO(String text, String type) {
            this.text = text;
            this.type = type;
        }
    }
}