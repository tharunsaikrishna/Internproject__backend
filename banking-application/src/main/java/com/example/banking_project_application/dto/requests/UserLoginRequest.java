package com.example.banking_project_application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for user login")
public class UserLoginRequest {

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Password of the user", example = "mypassword123")
    private String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserLoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}