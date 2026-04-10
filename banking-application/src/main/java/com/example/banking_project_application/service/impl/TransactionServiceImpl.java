package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.requests.*;
import com.example.banking_project_application.dto.responses.TransactionResponse;
import com.example.banking_project_application.entity.Account;
import com.example.banking_project_application.entity.Transaction;

import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.ResourceNotFoundException;

import com.example.banking_project_application.repositories.AccountRepository;
import com.example.banking_project_application.repositories.TransactionRepository;
import com.example.banking_project_application.repositories.UserRepository;
import com.example.banking_project_application.service.interfaces.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final UserRepository userRepo;

    public TransactionServiceImpl(AccountRepository accountRepo,
                                  TransactionRepository txRepo,
                                  UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {

        log.info("Deposit request: accountId={} amount={}",
                request.getAccountId(), request.getAmount());

        Account acc = getAccount(request.getAccountId());
        validate(acc, request.getAmount());

        acc.setBalance(acc.getBalance().add(request.getAmount()));
        accountRepo.save(acc);

        Transaction tx = new Transaction();
        tx.setToAccount(acc);
        tx.setAmount(request.getAmount());
        tx.setTransactionType(Transaction.TransactionType.CREDIT);
        tx.setStatus(Transaction.Status.SUCCESS);
        tx.setDescription("Deposit");

        Transaction savedTx = txRepo.save(tx);

        log.info("Deposit SUCCESS: accountId={} newBalance={} txId={}",
                acc.getAccountId(), acc.getBalance(), savedTx.getTransactionId());

        return map(savedTx);
    }


    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {

        log.info("Withdraw request: accountId={} amount={}",
                request.getAccountId(), request.getAmount());

        Account acc = getAccount(request.getAccountId());
        validate(acc, request.getAmount());

        if (acc.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Withdraw FAILED - insufficient balance: accountId={} balance={} requestAmount={}",
                    acc.getAccountId(), acc.getBalance(), request.getAmount());
            throw new InvalidRequestException("Insufficient balance");
        }

        acc.setBalance(acc.getBalance().subtract(request.getAmount()));
        accountRepo.save(acc);

        Transaction tx = new Transaction();
        tx.setFromAccount(acc);
        tx.setAmount(request.getAmount());
        tx.setTransactionType(Transaction.TransactionType.DEBIT);
        tx.setStatus(Transaction.Status.SUCCESS);
        tx.setDescription("Withdraw");

        Transaction savedTx = txRepo.save(tx);

        log.info("Withdraw SUCCESS: accountId={} newBalance={} txId={}",
                acc.getAccountId(), acc.getBalance(), savedTx.getTransactionId());

        return map(savedTx);
    }


    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        log.info("Transfer request: fromAccountId={} toAccountId={} amount={}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        Account from = getAccount(request.getFromAccountId());
        Account to = getAccount(request.getToAccountId());

        if (from.getAccountId().equals(to.getAccountId())) {
            log.warn("Transfer FAILED - same account used for from & to: accountId={}", from.getAccountId());
            throw new InvalidRequestException("Cannot transfer to same account");
        }

        validate(from, request.getAmount());
        validate(to, request.getAmount());

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Transfer FAILED - Insufficient balance: fromAccount={} balance={} requestAmount={}",
                    from.getAccountId(), from.getBalance(), request.getAmount());
            throw new InvalidRequestException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepo.save(from);
        accountRepo.save(to);

        Transaction tx = new Transaction();
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setAmount(request.getAmount());
        tx.setTransactionType(Transaction.TransactionType.TRANSFER);
        tx.setStatus(Transaction.Status.SUCCESS);
        tx.setDescription("Transfer");

        Transaction savedTx = txRepo.save(tx);

        log.info("Transfer SUCCESS: fromAccountId={} toAccountId={} amount={} txId={}",
                from.getAccountId(), to.getAccountId(), request.getAmount(), savedTx.getTransactionId());

        return map(savedTx);
    }


    @Override
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {

        log.info("Fetching transactions for accountId={}", accountId);

        List<Transaction> list = txRepo.findByFromAccount_AccountIdOrToAccount_AccountId(accountId, accountId);

        log.info("Total transactions found for accountId={} = {}", accountId, list.size());

        return list.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private Account getAccount(Long id) {
        log.debug("Fetching account from DB: accountId={}", id);

        return accountRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Account NOT FOUND: accountId={}", id);
                    return new ResourceNotFoundException("Account not found");
                });
    }

    private void validate(Account acc, BigDecimal amount) {

        log.debug("Validating account and amount: accountId={} amount={}",
                acc.getAccountId(), amount);

        if (acc.getStatus() == Account.Status.BLOCKED) {
            log.warn("Operation FAILED - Blocked account: accountId={}", acc.getAccountId());
            throw new InvalidRequestException("Account is blocked");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid amount detected: amount={} accountId={}", amount, acc.getAccountId());
            throw new InvalidRequestException("Invalid amount");
        }
    }

    private TransactionResponse map(Transaction tx) {

        log.debug("Mapping Transaction entity to DTO: txId={}", tx.getTransactionId());

        TransactionResponse res = new TransactionResponse();
        res.setTransactionId(tx.getTransactionId());
        res.setFromAccountId(tx.getFromAccount() != null ? tx.getFromAccount().getAccountId() : null);
        res.setToAccountId(tx.getToAccount() != null ? tx.getToAccount().getAccountId() : null);
        res.setType(tx.getTransactionType().name());
        res.setAmount(tx.getAmount());
        res.setStatus(tx.getStatus().name());
        res.setDescription(tx.getDescription());
        res.setTimestamp(tx.getTimestamp());

        return res;
    }
}