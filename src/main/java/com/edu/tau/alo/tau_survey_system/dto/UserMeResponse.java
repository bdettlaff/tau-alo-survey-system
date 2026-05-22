package com.edu.tau.alo.tau_survey_system.dto;

import com.edu.tau.alo.tau_survey_system.model.User.Role;

public class UserMeResponse {

    private Long id;
    private String login;
    private Role role;

    public UserMeResponse(Long id, String login, Role role) {
        this.id = id;
        this.login = login;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return role;
    }
}
