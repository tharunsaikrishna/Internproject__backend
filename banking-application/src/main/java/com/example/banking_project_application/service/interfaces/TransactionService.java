package com.example.banking_project_application.service.interfaces;

import com.example.banking_project_application.dto.requests.*;
import com.example.banking_project_application.dto.responses.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(DepositRequest request);

    TransactionResponse withdraw(WithdrawRequest request);

    TransactionResponse transfer(TransferRequest request);

    List<TransactionResponse> getTransactionsByAccount(Long accountId);
}