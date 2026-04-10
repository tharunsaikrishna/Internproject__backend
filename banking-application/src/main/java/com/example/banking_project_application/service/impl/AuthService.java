package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.exceptions.ResourceNotFoundException;
import com.example.banking_project_application.exceptions.UnauthorizedException;
import com.example.banking_project_application.repositories.AccountRepository;
import com.example.banking_project_application.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    public AuthService(UserRepository userRepo, AccountRepository accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
    }
    public void validateUserAccess(String username, Long userId) {

        log.info("Validating user access → username='{}' userId={}", username, userId);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User NOT FOUND during access validation: username='{}'", username);
                    return new ResourceNotFoundException("User not found");
                });

        if (!user.getUserId().equals(userId)) {
            log.warn("UNAUTHORIZED access attempt → username='{}' trying to access userId={}",
                    username, userId);

            throw new UnauthorizedException("You are not allowed to access this user");
        }

        log.info("User access VALIDATED successfully → username='{}' userId={}", username, userId);

    }


    public void validateAccountAccess(String username, Long accountId) {

        log.info("Validating account access → username='{}' accountId={}", username, accountId);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User NOT FOUND during account access check: username='{}'", username);
                    return new ResourceNotFoundException("User not found");
                });

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account NOT FOUND during access check: accountId={}", accountId);
                    return new ResourceNotFoundException("Account not found");
                });

        if (!account.getUser().getUserId().equals(user.getUserId())) {
            log.warn("UNAUTHORIZED account access attempt → username='{}' attempting accountId={} (belongs to userId={})",
                    username, accountId, account.getUser().getUserId());

            throw new UnauthorizedException("You are not allowed to access this account");
        }

        log.info("Account access VALIDATED successfully → username='{}' accountId={}",
                username, accountId);
    }
}