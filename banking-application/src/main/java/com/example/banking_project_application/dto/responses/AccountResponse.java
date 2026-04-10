package com.example.banking_project_application.dto.responses;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing account details")
@Data
public class AccountResponse {

    @Schema(description = "Unique ID of the account", example = "101")
    private Long accountId;

    @Schema(description = "ID of the user who owns the account", example = "10")
    private Long userId;

    @Schema(description = "Type of account", example = "SAVINGS")
    private String accountType;

    @Schema(description = "Current balance in the account", example = "15000.75")
    private BigDecimal balance;

    @Schema(description = "Account status", example = "ACTIVE")
    private String status;

    @Schema(description = "Date when the account was created", example = "2024-01-20T10:15:30")
    private Date createdAt;
}