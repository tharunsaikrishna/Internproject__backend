package com.example.banking_project_application.service.interfaces;


import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.responses.*;
import com.example.banking_project_application.entity.*;

import java.util.List;

public interface AdminService {
    Admin validateAdminLogin(String username, String password);
    Admin register(UserLoginRequest req);


    List<AccountResponse> getAccountsOfUser(Long userId);

    List<AccountResponse> getAllAccounts();

    // Users
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);

    void blockUser(Long userId);
    void unblockUser(Long userId);

    // Accounts
    void blockAccount(Long accountId);
    void unblockAccount(Long accountId);

    // Account Requests
    List<AccountRequestResponse> getPendingAccountRequests();
    void approveAccountRequest(Long requestId, String adminUsername);
    void rejectAccountRequest(Long requestId, String adminUsername);

    // Loans
    List<LoanResponse> getPendingLoans();
    List<LoanResponse> getApprovedLoans();
    List<LoanResponse> getRejectedLoans();
    void approveLoan(Long loanId, String adminUsername);
    void rejectLoan(Long loanId, String adminUsername);

    // Cheques
    List<ChequeBookResponse> getPendingChequeRequests();
    void approveCheque(Long requestId, String adminUsername);
    void rejectCheque(Long requestId, String adminUsername);
    List<TransactionResponse> getAllTransactions();

}
