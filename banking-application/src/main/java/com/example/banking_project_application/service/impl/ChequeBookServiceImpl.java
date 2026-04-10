package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.requests.ChequeBookRequestDto;
import com.example.banking_project_application.dto.responses.ChequeBookResponse;
import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.entity.ChequeBookRequest;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.exceptions.AccountBlockedException;
import com.example.banking_project_application.repositories.AccountRepository;
import com.example.banking_project_application.repositories.ChequeBookRequestRepository;
import com.example.banking_project_application.repositories.UserRepository;
import com.example.banking_project_application.service.interfaces.ChequeBookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChequeBookServiceImpl implements ChequeBookService {

    private static final Logger log =
            LoggerFactory.getLogger(ChequeBookServiceImpl.class);

    @Autowired
    private ChequeBookRequestRepository chequeRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ChequeBookResponse requestChequeBook(ChequeBookRequestDto dto) {

        log.info("Cheque book request initiated: userId={} accountId={}",
                dto.getUserId(), dto.getAccountId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("Cheque request FAILED - User not found userId={}", dto.getUserId());
                    return new RuntimeException("User not found");
                });

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> {
                    log.error("Cheque request FAILED - Account not found accountId={}", dto.getAccountId());
                    return new RuntimeException("Account not found");
                });

        // 🚫 BLOCKED ACCOUNT VALIDATION
        if (account.getStatus() == Account.Status.BLOCKED) {
            log.error("Cheque request FAILED - Account is BLOCKED accountId={} userId={}",
                    account.getAccountId(), dto.getUserId());

            throw new AccountBlockedException("Cheque book cannot be requested for a BLOCKED account.");
        }

        ChequeBookRequest request = new ChequeBookRequest();
        request.setUser(user);
        request.setAccount(account);
        request.setStatus(ChequeBookRequest.Status.PENDING);

        ChequeBookRequest saved = chequeRepo.save(request);

        log.info("Cheque book request CREATED: requestId={} userId={} accountId={}",
                saved.getRequestId(), user.getUserId(), account.getAccountId());

        return mapToResponse(saved);
    }
    @Override
    public List<ChequeBookResponse> getUserChequeRequests(Long userId) {

        log.info("Fetching cheque requests for userId={}", userId);

        List<ChequeBookResponse> list = chequeRepo.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Total cheque requests found for userId={} = {}", userId, list.size());

        return list;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public List<ChequeBookResponse> getApprovedChequeRequests(Long userId) {
       log.info("Fetching APPROVED cheque requests for userId={}", userId);

        List<ChequeBookResponse> list = chequeRepo.findByUser_UserIdAndStatus(userId, ChequeBookRequest.Status.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Total APPROVED cheque requests found for userId={} = {}", userId, list.size());

        return list;
    }



    /**
     * @param userId
     * @return
     */
    @Override
    public List<ChequeBookResponse> getPendingChequeRequests(Long userId) {
        log.info("Fetching PENDING cheque requests for userId={}", userId);

        List<ChequeBookResponse> list = chequeRepo.findByUser_UserIdAndStatus(userId, ChequeBookRequest.Status.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Total PENDING cheque requests found for userId={} = {}", userId, list.size());

        return list;
    }


     /**
     * @param userId
     * @return
     */
     @Override
     public List<ChequeBookResponse> getRejectedChequeRequests(Long userId) {
        log.info("Fetching REJECTED cheque requests for userId={}", userId);
        List<ChequeBookResponse> list = chequeRepo.findByUser_UserIdAndStatus(userId, ChequeBookRequest.Status.REJECTED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Total REJECTED cheque requests found for userId={} = {}", userId, list.size());
        return list;
    }

    private ChequeBookResponse mapToResponse(ChequeBookRequest req) {

        ChequeBookResponse res = new ChequeBookResponse();
        res.setRequestId(req.getRequestId());
        res.setUserId(req.getUser().getUserId());
        res.setAccountId(req.getAccount().getAccountId());
        res.setStatus(req.getStatus().name());
        res.setRequestDate(req.getRequestDate());

        return res;
    }
}