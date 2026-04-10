package com.example.banking_project_application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class AccountCreateRequest {

    @Schema(description = "Type of account user wants to create", example = "SAVINGS")
    private String accountType;
}
