package com.example.banking_project_application.repositories;

import com.example.banking_project_application.entity.ChequeBookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ChequeBookRequestRepository extends JpaRepository<ChequeBookRequest, Long> {

    List<ChequeBookRequest> findByUser_UserId(Long userId);

    List<ChequeBookRequest> findByAccount_AccountId(Long accountId);

    List<ChequeBookRequest> findByStatus(ChequeBookRequest.Status status);

    List<ChequeBookRequest> findByAdmin_AdminId(Long adminId);

    List<ChequeBookRequest> findByUser_UserIdAndStatus(Long userId, ChequeBookRequest.Status status);
}
