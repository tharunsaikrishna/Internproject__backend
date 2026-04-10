package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.responses.*;
import com.example.banking_project_application.entity.*;
import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.UnauthorizedException;
import com.example.banking_project_application.repositories.*;
import com.example.banking_project_application.service.interfaces.AdminService;

import com.example.banking_project_application.util.AccountIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log =
            LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired private UserRepository userRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private AccountRepository accountRepo;
    @Autowired private AccountRequestRepository accountRequestsRepo;
    @Autowired private LoanRequestRepository loanRepo;
    @Autowired private ChequeBookRequestRepository chequeRepo;
    @Autowired private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching ALL users (Admin)");
        List<User> users = userRepo.findAll();
        log.info("Total users found: {}", users.size());

        return users.stream()
                .map(u -> new UserResponse(
                        u.getUserId(),
                        u.getUsername(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getAddress(),
                        u.getStatus().name()
                ))
                .toList();
    }


    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user details for userId={} (Admin)", id);

        User u = userRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("User ID={} not found", id);
                    return new InvalidRequestException("User not found");
                });

        log.info("User details fetched for username='{}'", u.getUsername());

        return new UserResponse(
                u.getUserId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                u.getAddress(),
                u.getStatus().name()
        );
    }


    @Override
    public void blockUser(Long userId) {

        log.warn("Blocking userId={} (Admin action)", userId);

        User u = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("Block failed - userId={} NOT found", userId);
                    return new InvalidRequestException("User not found");
                });

        u.setStatus(User.Status.BLOCKED);
        userRepo.save(u);

        log.info("User BLOCKED successfully: username='{}'", u.getUsername());
    }


    @Override
    public void unblockUser(Long userId) {

        log.info("Unblocking userId={} (Admin action)", userId);

        User u = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("Unblock failed - userId={} NOT found", userId);
                    return new InvalidRequestException("User not found");
                });

        u.setStatus(User.Status.ACTIVE);
        userRepo.save(u);

        log.info("User UNBLOCKED successfully: username='{}'", u.getUsername());
    }

    @Override
    public void blockAccount(Long accountId) {

        log.warn("Blocking accountId={} (Admin action)", accountId);

        Account acc = accountRepo.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Block account failed - accountId={} NOT found", accountId);
                    return new InvalidRequestException("Account not found");
                });

        acc.setStatus(Account.Status.BLOCKED);
        accountRepo.save(acc);

        log.info("Account BLOCKED successfully: accountId={} userId={}",
                accountId, acc.getUser().getUserId());
    }


    @Override
    public void unblockAccount(Long accountId) {

        log.info("Unblocking accountId={} (Admin action)", accountId);

        Account acc = accountRepo.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Unblock account failed - accountId={} NOT found", accountId);
                    return new InvalidRequestException("Account not found");
                });

        acc.setStatus(Account.Status.ACTIVE);
        accountRepo.save(acc);

        log.info("Account UNBLOCKED successfully: accountId={} userId={}",
                accountId, acc.getUser().getUserId());
    }

    @Override
    public List<AccountRequestResponse> getPendingAccountRequests() {
        log.info("Fetching PENDING account requests (Admin)");
        List<AccountRequestResponse> list = accountRequestsRepo.findByStatus(AccountRequest.Status.PENDING)
                .stream()
                .map(r -> {
                    AccountRequestResponse res = new AccountRequestResponse();
                    res.setRequestId(r.getRequestId());
                    res.setUserId(r.getUser().getUserId());
                    res.setAccountType(r.getAccountType().name());
                    res.setStatus(r.getStatus().name());
                    return res;
                })
                .toList();
        log.info("Total pending account requests found: {}", list.size());
        return list;
    }


    @Override
    public void approveAccountRequest(Long requestId, String adminUser) {

        log.info("Approving account requestId={} by admin='{}'", requestId, adminUser);

        AccountRequest req = accountRequestsRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Account request NOT FOUND: requestId={}", requestId);
                    return new InvalidRequestException("Request not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Admin username='{}' not found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        req.setStatus(AccountRequest.Status.APPROVED);
        req.setAdmin(admin);
        accountRequestsRepo.save(req);

        Account account = new Account();
        account.setUser(req.getUser());
        account.setAccountId(AccountIdGenerator.generateUniqueId(accountRepo));
        account.setAccountType(req.getAccountType());
        account.setStatus(Account.Status.ACTIVE);
        account.setBalance(BigDecimal.ZERO);

        accountRepo.save(account);

        log.info("Account request APPROVED: requestId={} newAccountId={} userId={}",
                requestId, account.getAccountId(), req.getUser().getUserId());
    }


    @Override
    public void rejectAccountRequest(Long requestId, String adminUser) {

        log.warn("Rejecting account requestId={} by admin='{}'", requestId, adminUser);

        AccountRequest req = accountRequestsRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Reject failed - account requestId={} NOT found", requestId);
                    return new InvalidRequestException("Request not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Reject failed - admin '{}' NOT found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        req.setStatus(AccountRequest.Status.REJECTED);
        req.setAdmin(admin);
        accountRequestsRepo.save(req);

        log.info("Account request REJECTED successfully: requestId={}", requestId);
    }


    @Override
    public List<AccountResponse> getAccountsOfUser(Long userId) {
        log.info("Fetching all accounts of userId={}", userId);

        if (!userRepo.existsById(userId)) {
            log.error("User NOT found: userId={}", userId);
            throw new InvalidRequestException("User not found");
        }

        List<AccountResponse> list = accountRepo.findByUser_UserId(userId)
                .stream()
                .map(a -> {
                    AccountResponse res = new AccountResponse();
                    res.setAccountId(a.getAccountId());
                    res.setAccountType(a.getAccountType().name());
                    res.setBalance(a.getBalance());
                    res.setStatus(a.getStatus().name());
                    return res;
                })
                .toList();
        log.info("Total accounts found for userId={} = {}", userId, list.size());
        return list;
    }


    @Override
    public List<AccountResponse> getAllAccounts() {
        log.info("Fetching ALL accounts (Admin view)");
        List<AccountResponse> list = accountRepo.findAll()
                .stream()
                .map(a -> {
                    AccountResponse res = new AccountResponse();
                    res.setAccountId(a.getAccountId());
                    res.setUserId(a.getUser().getUserId());
                    res.setAccountType(a.getAccountType().name());
                    res.setBalance(a.getBalance());
                    res.setStatus(a.getStatus().name());
                    return res;
                })
                .toList();
        log.info("Total accounts found: {}", list.size());
        return list;
    }


    @Override
    public List<LoanResponse> getPendingLoans() {
        log.info("Fetching PENDING loan requests");
        List<LoanResponse> list = loanRepo.findByStatus(LoanRequest.Status.PENDING)
                .stream()
                .map(l ->{
                    LoanResponse res = new LoanResponse();
                    res.setLoanId(l.getLoanId());
                    res.setUserId(l.getUser().getUserId());
                    res.setLoanAmount(l.getLoanAmount());
                    res.setLoanType(l.getLoanType());
                    res.setStatus(l.getStatus().name());
                    res.setRequestDate(l.getRequestDate());
                    return res;
                })
                .toList();
        log.info("Total pending loan requests = {}", list.size());
        return list;
    }

    @Override
    public List<LoanResponse> getApprovedLoans() {
        log.info("Fetching APPROVED loan requests");
        List<LoanResponse> list = loanRepo.findByStatus(LoanRequest.Status.APPROVED)
                .stream()
                .map(l ->{
                    LoanResponse res = new LoanResponse();
                    res.setLoanId(l.getLoanId());
                    res.setUserId(l.getUser().getUserId());
                    res.setLoanAmount(l.getLoanAmount());
                    res.setLoanType(l.getLoanType());
                    res.setStatus(l.getStatus().name());
                    res.setRequestDate(l.getRequestDate());
                    return res;
                })
                .toList();
        log.info("Total approved loan requests = {}", list.size());
        return list;
    }

    @Override
    public List<LoanResponse> getRejectedLoans() {
        log.info("Fetching REJECTED loan requests");
        List<LoanResponse> list = loanRepo.findByStatus(LoanRequest.Status.REJECTED)
                .stream()
                .map(l ->{
                    LoanResponse res = new LoanResponse();
                    res.setLoanId(l.getLoanId());
                    res.setUserId(l.getUser().getUserId());
                    res.setLoanAmount(l.getLoanAmount());
                    res.setLoanType(l.getLoanType());
                    res.setStatus(l.getStatus().name());
                    res.setRequestDate(l.getRequestDate());
                    return res;
                })
                .toList();
        log.info("Total rejected loan requests = {}", list.size());
        return list;
    }


    @Override
    public void approveLoan(Long loanId, String adminUser) {

        log.info("Approving loanId={} by admin='{}'", loanId, adminUser);

        LoanRequest loan = loanRepo.findById(loanId)
                .orElseThrow(() -> {
                    log.error("Loan NOT found loanId={}", loanId);
                    return new InvalidRequestException("Loan not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Admin '{}' not found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        loan.setStatus(LoanRequest.Status.APPROVED);
        loan.setAdmin(admin);
        loan.setApproveDate(new Date());
        loanRepo.save(loan);

        log.info("Loan APPROVED: loanId={} userId={}", loanId, loan.getUser().getUserId());
    }


    @Override
    public void rejectLoan(Long loanId, String adminUser) {

        log.warn("Rejecting loanId={} by admin='{}'", loanId, adminUser);

        LoanRequest loan = loanRepo.findById(loanId)
                .orElseThrow(() -> {
                    log.error("Loan reject failed - loanId={} NOT found", loanId);
                    return new InvalidRequestException("Loan not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Loan reject failed - admin '{}' not found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        loan.setStatus(LoanRequest.Status.REJECTED);
        loan.setAdmin(admin);
        loan.setApproveDate(new Date());
        loanRepo.save(loan);

        log.info("Loan REJECTED: loanId={} userId={}", loanId, loan.getUser().getUserId());
    }

    @Override
    public List<ChequeBookResponse> getPendingChequeRequests() {
        log.info("Fetching PENDING cheque requests");
        List<ChequeBookResponse> list = chequeRepo.findByStatus(ChequeBookRequest.Status.PENDING)
                .stream()
                .map(c -> {
                    ChequeBookResponse res = new ChequeBookResponse();
                    res.setRequestId(c.getRequestId());
                    res.setUserId(c.getUser().getUserId());
                    res.setAccountId(c.getAccount().getAccountId());
                    res.setStatus(c.getStatus().name());
                    res.setRequestDate(c.getRequestDate());
                    return res;
                })
                .toList();
        log.info("Total pending cheque requests = {}", list.size());
        return list;
    }


    @Override
    public void approveCheque(Long requestId, String adminUser) {

        log.info("Approving cheque requestId={} by admin='{}'", requestId, adminUser);

        ChequeBookRequest c = chequeRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Cheque requestId={} not found", requestId);
                    return new InvalidRequestException("Cheque request not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Admin '{}' not found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        c.setStatus(ChequeBookRequest.Status.APPROVED);
        c.setAdmin(admin);

        chequeRepo.save(c);

        log.info("Cheque request APPROVED: requestId={} userId={}",
                requestId, c.getUser().getUserId());
    }

    @Override
    public void rejectCheque(Long requestId, String adminUser) {

        log.warn("Rejecting cheque requestId={} by admin='{}'", requestId, adminUser);

        ChequeBookRequest c = chequeRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Reject failed - cheque requestId={} not found", requestId);
                    return new InvalidRequestException("Cheque request not found");
                });

        Admin admin = adminRepo.findByUsername(adminUser)
                .orElseThrow(() -> {
                    log.error("Admin '{}' not found", adminUser);
                    return new InvalidRequestException("Admin not found");
                });

        c.setStatus(ChequeBookRequest.Status.REJECTED);
        c.setAdmin(admin);

        chequeRepo.save(c);

        log.info("Cheque request REJECTED: requestId={} userId={}",
                requestId, c.getUser().getUserId());
    }



    @Override
    public Admin register(UserLoginRequest req) {

        log.info("Admin registration attempt for username='{}'", req.getUsername());

        if (adminRepo.existsByUsername(req.getUsername())) {
            log.warn("Admin registration FAILED - username='{}' already exists", req.getUsername());
            throw new InvalidRequestException("Admin username already exists.");
        }

        Admin admin = new Admin();
        admin.setUsername(req.getUsername());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));

        Admin saved = adminRepo.save(admin);

        log.info("Admin registered successfully: username='{}' id={}",
                saved.getUsername(), saved.getAdminId());

        return saved;
    }

    @Override
    public Admin validateAdminLogin(String username, String password) {

        log.info("Admin login attempt username='{}'", username);

        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Admin login FAILED - username '{}' not found", username);
                    return new UnauthorizedException("Invalid admin credentials.");
                });

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            log.warn("Admin login FAILED - wrong password for username '{}'", username);
            throw new UnauthorizedException("Invalid admin credentials.");
        }

        log.info("Admin login SUCCESS username='{}'", username);
        return admin;
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> obj=transactionRepository.findAll();
        System.out.println(obj);
        List<TransactionResponse> objresponse= obj.stream().map(
                t ->{
                    TransactionResponse tr=new TransactionResponse();
                    tr.setTransactionId(t.getTransactionId());
                    tr.setAmount(t.getAmount());
                    tr.setDescription(t.getDescription());
                    tr.setTimestamp(t.getTimestamp());
                    tr.setStatus(String.valueOf(t.getStatus()));
                    tr.setType(String.valueOf(t.getTransactionType()));
                    tr.setFromAccountId(t.getFromAccount() != null ? t.getFromAccount().getAccountId() : null);
                    tr.setToAccountId(t.getToAccount() != null ? t.getToAccount().getAccountId() : null);
                    return tr;

                }
        ).toList();
        return objresponse;
    }




}