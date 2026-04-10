package com.example.banking_project_application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing user profile details")
public class UserResponse {

    @Schema(description = "Unique ID of the user", example = "10")
    private Long id;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "9876543210")
    private String phone;

    @Schema(description = "Residential address of the user", example = "123 MG Road, Bangalore")
    private String address;

    @Schema(description = "Current status of the user account", example = "ACTIVE")
    private String status;

    public UserResponse(Long id, String username, String fullName, String email, String phone, String address, String status) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    public UserResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}