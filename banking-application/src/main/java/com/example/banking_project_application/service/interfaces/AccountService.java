package com.example.banking_project_application.service.interfaces;

import com.example.banking_project_application.dto.responses.AccountResponse;

import java.util.List;

public interface AccountService {

    List<AccountResponse> getUserAccounts(Long userId);

    AccountResponse getAccountById(Long accountId);

    Double getBalance(Long accountId);
}

