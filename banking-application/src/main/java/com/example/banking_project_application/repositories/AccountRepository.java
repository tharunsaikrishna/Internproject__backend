package com.example.banking_project_application.repositories;

import com.example.banking_project_application.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser_UserId(Long userId);

    Account findByAccountId(Long accountId);

    boolean existsByAccountIdAndStatus(Long accountId, Account.Status status);
}