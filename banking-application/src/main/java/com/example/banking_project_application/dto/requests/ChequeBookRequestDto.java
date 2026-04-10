package com.example.banking_project_application.dto.requests;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChequeBookRequestDto {
    @Schema(description = "ID of the user requesting cheque book", example = "10")    private Long userId;
    private Long accountId;

    @Schema(description = "Account ID for which cheque book is requested", example = "2001")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }


}