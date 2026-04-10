package com.example.banking_project_application.dto.responses;

import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.entity.AccountRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountRequestResponse
{
    private Long requestId;
    private Long userId;
    private String accountType;
    private String status;


}
