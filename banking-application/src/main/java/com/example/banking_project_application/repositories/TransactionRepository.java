package com.example.banking_project_application.repositories;


import com.example.banking_project_application.dto.responses.TransactionResponse;
import com.example.banking_project_application.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Outgoing transactions
    List<Transaction> findByFromAccount_AccountId(Long accountId);

    // Incoming transactions
    List<Transaction> findByToAccount_AccountId(Long accountId);

    // All activity of an account
    List<Transaction> findByFromAccount_AccountIdOrToAccount_AccountId(Long fromId, Long toId);


}