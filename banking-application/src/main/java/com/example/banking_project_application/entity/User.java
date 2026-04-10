package com.example.banking_project_application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username is required")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{3,49}$",
            message = "Username must start with a letter and contain only letters, numbers, underscores (4–50 chars)"
    )
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password cannot be empty")

    private String password;

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10-15 digits and may start with +"
    )
    private String phone;

    @Size(max = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    private Date createdAt;

    public enum Status { ACTIVE, BLOCKED }
}

