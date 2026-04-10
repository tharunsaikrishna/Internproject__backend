package com.example.banking_project_application.service.impl;

import com.example.banking_project_application.dto.requests.LoanRequestDto;
import com.example.banking_project_application.dto.responses.LoanResponse;
import com.example.banking_project_application.entity.LoanRequest;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.repositories.LoanRequestRepository;
import com.example.banking_project_application.repositories.UserRepository;
import com.example.banking_project_application.service.interfaces.LoanService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger log =
            LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    private LoanRequestRepository loanRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public LoanResponse applyLoan(LoanRequestDto dto) {

        log.info("Loan application started: userId={} amount={} type={}",
                dto.getUserId(), dto.getLoanAmount(), dto.getLoanType());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("Loan application FAILED - User not found userId={}", dto.getUserId());
                    return new RuntimeException("User not found");
                });

        LoanRequest loan = new LoanRequest();
        loan.setUser(user);
        loan.setLoanAmount(dto.getLoanAmount());
        loan.setLoanType(dto.getLoanType());
        loan.setStatus(LoanRequest.Status.PENDING);

        LoanRequest saved = loanRepository.save(loan);

        log.info("Loan request CREATED: loanId={} userId={}",
                saved.getLoanId(), user.getUserId());

        return mapToResponse(saved);
    }


    @Override
    public List<LoanResponse> getUserLoans(Long userId) {

        log.info("Fetching loan requests for userId={}", userId);

        List<LoanResponse> list = loanRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Total loans found for userId={} = {}", userId, list.size());

        return list;
    }

    @Override
    public List<LoanResponse> getApprovedUserLoans(Long userId) {
        log.info("Fetching APPROVED loan requests for userId={}", userId);
        List<LoanResponse> list = loanRepository.findByUser_UserIdAndStatus(userId, LoanRequest.Status.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Total APPROVED loans found for userId={} = {}", userId, list.size());
        return list;
    }

    @Override
    public List<LoanResponse> getRejectedUserLoans(Long userId) {
        log.info("Fetching REJECTED loan requests for userId={}", userId);
        List<LoanResponse> list = loanRepository.findByUser_UserIdAndStatus(userId, LoanRequest.Status.REJECTED)

                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Total REJECTED loans found for userId={} = {}", userId, list.size());
        return list;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public List<LoanResponse> getPendingUserLoans(Long userId) {
        log.info("Fetching PENDING loan requests for userId={}", userId);
        List<LoanResponse> list = loanRepository.findByUser_UserIdAndStatus(userId, LoanRequest.Status.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Total PENDING loans found for userId={} = {}", userId, list.size());
        return list;
    }

    private LoanResponse mapToResponse(LoanRequest loan) {


        LoanResponse res = new LoanResponse();
        res.setLoanId(loan.getLoanId());
        res.setUserId(loan.getUser().getUserId());
        res.setLoanAmount(loan.getLoanAmount());
        res.setLoanType(loan.getLoanType());
        res.setStatus(loan.getStatus().name());
        res.setRequestDate(loan.getRequestDate());

        return res;
    }
}