package com.example.banking_project_application.service.interfaces;

import com.example.banking_project_application.dto.responses.AccountRequestResponse;
import com.example.banking_project_application.entity.AccountRequest;
import com.example.banking_project_application.dto.requests.AccountCreateRequest;

import java.util.List;

public interface AccountRequestService {

    void createAccountRequest(Long userId, AccountCreateRequest request);
    
    List<AccountRequestResponse> getUserAccountRequests(Long userId);
}