package com.example.banking_project_application.dto.responses;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing loan request details")
@Data
public class LoanResponse {

    @Schema(description = "Unique ID of the loan request", example = "301")
    private Long loanId;

    @Schema(description = "ID of the user who applied for the loan", example = "10")
    private Long userId;

    @Schema(description = "Amount of loan requested", example = "50000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Type of loan requested", example = "PERSONAL")
    private String loanType;

    @Schema(description = "Current status of the loan request", example = "APPROVED")
    private String status;

    @Schema(description = "Date when the loan request was submitted", example = "2024-01-18T11:45:00")
    private Date requestDate;

}