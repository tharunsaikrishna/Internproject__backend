package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.requests.AccountCreateRequest;
import com.example.banking_project_application.dto.responses.AccountRequestResponse;
import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.entity.AccountRequest;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.ResourceNotFoundException;
import com.example.banking_project_application.repositories.AccountRequestRepository;
import com.example.banking_project_application.repositories.UserRepository;
import com.example.banking_project_application.service.interfaces.AccountRequestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRequestServiceImpl implements AccountRequestService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountRequestServiceImpl.class);

    @Autowired
    private AccountRequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public void createAccountRequest(Long userId, AccountCreateRequest request) {

        log.info("Account request creation started for userId={}", userId);

        if (userId == null || userId <= 0) {
            log.warn("Invalid User ID received: {}", userId);
            throw new InvalidRequestException("Invalid User ID: " + userId);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID={}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        if (request == null) {
            log.warn("Account request creation failed - body is null (userId={})", userId);
            throw new InvalidRequestException("Account request body cannot be null.");
        }

        if (request.getAccountType() == null) {
            log.warn("Account request missing accountType (userId={})", userId);
            throw new InvalidRequestException("Account type is required.");
        }

        String type = request.getAccountType().trim().toUpperCase();
        log.debug("Received account type='{}' for userId={}", type, userId);

        if (!type.equals("PRIMARY") && !type.equals("SAVINGS")) {
            log.warn("Invalid account type='{}' for userId={}", type, userId);
            throw new InvalidRequestException(
                    "Invalid account type: " + type + ". Allowed: PRIMARY, SAVINGS"
            );
        }

        AccountRequest accReq = new AccountRequest();
        accReq.setUser(user);
        accReq.setAccountType(Account.AccountType.valueOf(type));
        accReq.setStatus(AccountRequest.Status.PENDING);

        AccountRequest saved = requestRepo.save(accReq);

        log.info("Account request CREATED successfully: requestId={} userId={} type={}",
                saved.getRequestId(), userId, type);


    }


    @Override
    public java.util.List<AccountRequestResponse> getUserAccountRequests(Long userId) {

        log.info("Fetching all account requests for userId={}", userId);

        if (userId == null || userId <= 0) {
            log.warn("Invalid User ID in getUserAccountRequests: {}", userId);
            throw new InvalidRequestException("Invalid User ID: " + userId);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found while fetching account requests, userId={}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        log.debug("User found: username='{}' userId={}", user.getUsername(), userId);

        java.util.List<AccountRequestResponse> list = requestRepo.findByUser_UserId(userId)
                .stream()
                .map(req -> {
                    log.trace("Mapping AccountRequest to AccountRequestResponse: requestId={} status={}",
                            req.getRequestId(), req.getStatus().name());

                    AccountRequestResponse resp = new AccountRequestResponse();
                    resp.setRequestId(req.getRequestId());
                    resp.setUserId(userId);
                    resp.setAccountType(req.getAccountType().name());
                    resp.setStatus(req.getStatus().name());
                    return resp;
                })
                .toList();

        log.info("Retrieved {} account requests for userId={}", list.size(), userId);

        return list;
    }
}