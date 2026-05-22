package com.edu.tau.alo.tau_survey_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Questions")
public class Question {

    @Id
    private String id;
    private String content;
    private String type;

    @Column(name = "is_active")
    private Boolean isActive = true;
    private String module;

    @Column(name = "class_requirement")
    private String classRequirement;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public Boolean getIsActive() { return isActive; }
    public String getModule() { return module; }
    public String getClassRequirement() { return classRequirement; }
    public QuestionCategory getCategory() { return category; }

    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setType(String type) { this.type = type; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setModule(String module) { this.module = module; }
    public void setClassRequirement(String classRequirement) { this.classRequirement = classRequirement; }
    public void setCategory(QuestionCategory category) { this.category = category; }
}