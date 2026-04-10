package com.example.banking_project_application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "admin")
@Data
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{3,49}$",
            message = "Username must be valid"
    )
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    private String password;

    @CreationTimestamp
    private Date createdAt;
}