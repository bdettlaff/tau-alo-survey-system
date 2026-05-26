package com.edu.tau.alo.tau_survey_system.dto;

public class CommentDTO {
    private String text;
    private String type; // np. "POZYTYWNA", "KONSTRUKTYWNA", "INTERNAL"

    public CommentDTO(String text, String type) {
        this.text = text;
        this.type = type;
    }

    // Gettery i settery
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}