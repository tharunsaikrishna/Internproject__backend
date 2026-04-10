package com.example.banking_project_application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "loan_requests")
@Data
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @DecimalMin("1000.00")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal loanAmount;

    @NotBlank
    @Size(max = 50)
    private String loanType;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @CreationTimestamp
    private Date requestDate;

    private Date approveDate;

    public enum Status { PENDING, APPROVED, REJECTED }
}