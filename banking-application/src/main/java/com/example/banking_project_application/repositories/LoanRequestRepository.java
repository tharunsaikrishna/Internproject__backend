package com.example.banking_project_application.repositories;

import com.example.banking_project_application.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    List<LoanRequest> findByUser_UserId(Long userId);

    List<LoanRequest> findByAdmin_AdminId(Long adminId);

    List<LoanRequest> findByStatus(LoanRequest.Status status);

    List<LoanRequest> findByUser_UserIdAndStatus(Long userId, LoanRequest.Status status);
}