package com.example.banking_project_application.dto.responses;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing details of a transaction")
public class TransactionResponse {

    @Schema(description = "Unique ID of the transaction", example = "9001")
    private Long transactionId;

    @Schema(description = "Account ID from which money was transferred", example = "2001")
    private Long fromAccountId;

    @Schema(description = "Account ID to which money was transferred", example = "3005")
    private Long toAccountId;

    @Schema(description = "Type of transaction", example = "TRANSFER")
    private String type;

    @Schema(description = "Transaction amount", example = "2500.00")
    private BigDecimal amount;

    @Schema(description = "Status of the transaction", example = "SUCCESS")
    private String status;

    @Schema(description = "Description or note related to the transaction", example = "Payment for bills")
    private String description;

    @Schema(description = "Timestamp of when the transaction occurred", example = "2024-01-20T15:45:00")
    private Date timestamp;

    // Getters & Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}