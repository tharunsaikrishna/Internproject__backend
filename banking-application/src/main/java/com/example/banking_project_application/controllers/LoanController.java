package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.requests.LoanRequestDto;
import com.example.banking_project_application.dto.responses.LoanResponse;
import com.example.banking_project_application.service.impl.AuthService;
import com.example.banking_project_application.service.interfaces.LoanService;
import com.example.banking_project_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loan Management", description = "APIs for loan requests and management")
public class LoanController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for loan", description = "Allows an authenticated user to submit a loan application request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application submitted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid loan request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public LoanResponse applyLoan(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody LoanRequestDto dto
    ) {
        dto.setUserId(userService.findByUsername(userDetails.getUsername()).getUserId());
        return loanService.applyLoan(dto);
    }

    @GetMapping("/user")
    @Operation(summary = "Get user loans", description = "Retrieves all loan applications submitted by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getUserLoans(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(loanService.getUserLoans(userId));

    }


    @GetMapping("/user/approved")
    @Operation(summary = "Get approved user loans", description = "Retrieves all approved loan applications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Approved user loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getApprovedUserLoans(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(loanService.getApprovedUserLoans(userId));
        }
    @GetMapping("/user/rejected")
    @Operation(summary = "Get rejected user loans", description = "Retrieves all rejected loan applications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rejected user loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getRejectedUserLoans(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(loanService.getRejectedUserLoans(userId));
    }
    @GetMapping("user/pending")
    @Operation(summary = "Get pending user loans", description = "Retrieves all pending loan applications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending user loans retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getPendingUserLoans(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(loanService.getPendingUserLoans(userId));
    }
}
