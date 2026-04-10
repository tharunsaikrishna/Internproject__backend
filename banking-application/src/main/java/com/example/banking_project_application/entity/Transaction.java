package com.example.banking_project_application.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;


    @Enumerated(EnumType.STRING)
    private Status status;

    @Size(max = 255)
    private String description;

    @CreationTimestamp
    private Date timestamp;

    public enum TransactionType { DEBIT, CREDIT,TRANSFER }
    public enum Status { SUCCESS, FAILED }
}