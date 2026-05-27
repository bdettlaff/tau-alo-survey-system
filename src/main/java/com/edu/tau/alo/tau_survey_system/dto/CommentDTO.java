package com.edu.tau.alo.tau_survey_system.dto;

public class CommentDTO {
    private String text;
    private String type; // "POZYTYWNA" / "KONSTRUKTYWNA" / "INTERNAL"
    private String questionText; // NOWE — treść pytania

    public CommentDTO() {}

    public CommentDTO(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public CommentDTO(String text, String type, String questionText) {
        this.text = text;
        this.type = type;
        this.questionText = questionText;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}
