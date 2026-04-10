package com.example.banking_project_application.service.interfaces;

import com.example.banking_project_application.dto.requests.ChequeBookRequestDto;
import com.example.banking_project_application.dto.responses.ChequeBookResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ChequeBookService {

    ChequeBookResponse requestChequeBook(ChequeBookRequestDto dto);

    List<ChequeBookResponse> getUserChequeRequests(Long userId);

    List<ChequeBookResponse> getApprovedChequeRequests(Long userId);

    List<ChequeBookResponse> getPendingChequeRequests(Long userId);

    List<ChequeBookResponse> getRejectedChequeRequests(Long userId);
}