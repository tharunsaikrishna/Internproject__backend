package com.example.banking_project_application.dto.requests;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for transferring money between accounts")
public class TransferRequest {

    @Schema(description = "Source account ID from which amount will be deducted", example = "1001")
    private Long fromAccountId;

    @Schema(description = "Destination account ID to which amount will be added", example = "2001")
    private Long toAccountId;

    @Schema(description = "Amount to be transferred", example = "2500.00")
    private BigDecimal amount;

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
