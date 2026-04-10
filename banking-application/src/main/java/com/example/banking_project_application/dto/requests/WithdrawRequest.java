package com.example.banking_project_application.dto.requests;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for withdrawing money from an account")
public class WithdrawRequest {

    @Schema(description = "ID of the account to withdraw from", example = "3001")
    private Long accountId;

    @Schema(description = "Amount to withdraw", example = "500.00")
    private BigDecimal amount;

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}