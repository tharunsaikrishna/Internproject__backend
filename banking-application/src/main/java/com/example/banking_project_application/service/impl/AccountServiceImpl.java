package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.responses.AccountResponse;
import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.ResourceNotFoundException;
import com.example.banking_project_application.exceptions.UnauthorizedException;
import com.example.banking_project_application.repositories.AccountRepository;
import com.example.banking_project_application.service.interfaces.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepo;


    @Override
    public List<AccountResponse> getUserAccounts(Long userId) {

        log.info("Fetching all accounts for userId={}", userId);

        if (userId == null || userId <= 0) {
            log.warn("Invalid User ID received: {}", userId);
            throw new InvalidRequestException("Invalid User ID: " + userId);
        }

        List<Account> accounts = accountRepo.findByUser_UserId(userId);

        if (accounts.isEmpty()) {
            log.error("No accounts found for userId={}", userId);
            throw new ResourceNotFoundException("No accounts found for user ID: " + userId);
        }

        log.info("Found {} accounts for userId={}", accounts.size(), userId);

        return accounts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public AccountResponse getAccountById(Long accountId) {

        log.info("Fetching account details for accountId={}", accountId);

        if (accountId == null || accountId <= 0) {
            log.warn("Invalid Account ID received: {}", accountId);
            throw new InvalidRequestException("Invalid Account ID: " + accountId);
        }

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with ID={}", accountId);
                    return new ResourceNotFoundException("Account not found with ID: " + accountId);
                });

        log.info("Account found: accountId={} userId={} type={}",
                account.getAccountId(),
                account.getUser().getUserId(),
                account.getAccountType().name()
        );

        return toResponse(account);
    }


    @Override
    public Double getBalance(Long accountId) {

        log.info("Balance check request for accountId={}", accountId);

        if (accountId == null || accountId <= 0) {
            log.warn("Invalid Account ID: {}", accountId);
            throw new InvalidRequestException("Invalid Account ID: " + accountId);
        }

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Balance check failed - accountId={} NOT FOUND", accountId);
                    return new ResourceNotFoundException("Account not found with ID: " + accountId);
                });

        if (account.getStatus() == Account.Status.BLOCKED) {
            log.warn("Blocked account balance check attempt - accountId={} userId={}",
                    accountId, account.getUser().getUserId());
            throw new UnauthorizedException("Account is BLOCKED. Cannot view balance.");
        }

        log.info("Balance retrieved for accountId={} balance={}", accountId, account.getBalance());

        return account.getBalance().doubleValue();
    }


    private AccountResponse toResponse(Account acc) {

        log.debug("Converting Account entity to AccountResponse for accountId={}", acc.getAccountId());

        AccountResponse res = new AccountResponse();
        res.setAccountId(acc.getAccountId());
        res.setUserId(acc.getUser().getUserId());
        res.setAccountType(acc.getAccountType().name());
        res.setBalance(acc.getBalance());
        res.setStatus(acc.getStatus().name());
        res.setCreatedAt(acc.getCreatedAt());

        return res;
    }
}