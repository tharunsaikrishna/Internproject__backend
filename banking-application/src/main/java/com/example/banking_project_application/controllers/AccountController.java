package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.requests.AccountCreateRequest;
import com.example.banking_project_application.dto.responses.AccountRequestResponse;
import com.example.banking_project_application.dto.responses.AccountResponse;
import com.example.banking_project_application.entity.AccountRequest;
import com.example.banking_project_application.service.impl.AuthService;
import com.example.banking_project_application.service.interfaces.AccountService;
import com.example.banking_project_application.service.interfaces.AccountRequestService;

import com.example.banking_project_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/user/accounts")
@Tag(name = "Account Management", description = "APIs for managing user bank accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private AccountRequestService requestService;

    @PostMapping("/request-create")
    @Operation(summary = "Request account creation", 
        description = "Allows an authenticated user to submit a request for creating a new bank account. The request requires admin approval.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account request created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountRequest.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<String> createAccountRequest(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AccountCreateRequest request
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        requestService.createAccountRequest(userId, request);
        return ResponseEntity.ok("Account creation request submitted successfully and is pending approval.");

    }

    @GetMapping("/")
    @Operation(summary = "Get all user accounts", 
        description = "Retrieves all bank accounts associated with the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        List<AccountResponse> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }
    

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details", 
        description = "Retrieves details for a specific bank account by its ID. Users can only access their own accounts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authorized to access this account"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Account ID", required = true, example = "5432")
            @PathVariable Long accountId) {
        authService.validateAccountAccess(userDetails.getUsername(), accountId);
        AccountResponse account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }


    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance", 
        description = "Retrieves the current balance of a specific bank account. Users can only view balances for their own accounts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account balance retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "number", format = "double", example = "10000.50"))),
        @ApiResponse(responseCode = "401", description = "User not authorized to access this account"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Double> getBalance(@AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Account ID", required = true, example = "5432")
            @PathVariable Long accountId) {
        authService.validateAccountAccess(userDetails.getUsername(), accountId);
        Double balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/requests")
    @Operation(summary = "Get user account requests", 
        description = "Retrieves all account creation requests submitted by the authenticated user along with their current status (pending, approved, rejected)")

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountRequest.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<AccountRequestResponse>> getUserAccountRequests(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        List<AccountRequestResponse> requests = requestService.getUserAccountRequests(userId);
        return ResponseEntity.ok(requests);
    }
}