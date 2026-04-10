package com.example.banking_project_application.service.interfaces;

import com.example.banking_project_application.dto.requests.LoanRequestDto;
import com.example.banking_project_application.dto.responses.LoanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface LoanService {

    LoanResponse applyLoan(LoanRequestDto dto);

    List<LoanResponse> getUserLoans(Long userId);

    List<LoanResponse> getApprovedUserLoans(Long userId);

    List<LoanResponse> getRejectedUserLoans(Long userId);

    List<LoanResponse> getPendingUserLoans(Long userId);
}