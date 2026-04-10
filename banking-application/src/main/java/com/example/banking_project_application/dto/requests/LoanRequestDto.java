package com.example.banking_project_application.dto.requests;

import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for applying a loan")
@Data
public class LoanRequestDto {

    @Schema(description = "ID of the user applying for the loan", example = "10")
    private Long userId;

    @Schema(description = "Loan amount requested by the user", example = "50000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Type of loan", example = "HOME, PERSONAL, EDUCATION")
    private String loanType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
}