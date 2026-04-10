package com.example.banking_project_application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    private Long accountId;


    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @DecimalMin("0.00")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private Date createdAt;

    public enum AccountType { PRIMARY, SAVINGS }
    
    public enum Status { ACTIVE, BLOCKED }
}