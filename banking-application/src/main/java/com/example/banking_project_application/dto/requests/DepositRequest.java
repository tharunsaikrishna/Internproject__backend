package com.example.banking_project_application.dto.requests;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for depositing money into an account")
public class DepositRequest {

    @Schema(description = "ID of the account to deposit into", example = "2001")
    private Long accountId;

    @Schema(description = "Amount to deposit", example = "1500.50")
    private BigDecimal amount;


    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}