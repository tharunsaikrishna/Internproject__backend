package com.example.banking_project_application.repositories;


import com.example.banking_project_application.entity.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    List<AccountRequest> findByUser_UserId(Long userId);

    List<AccountRequest> findByStatus(AccountRequest.Status status);

    List<AccountRequest> findByAdmin_AdminId(Long adminId);
}