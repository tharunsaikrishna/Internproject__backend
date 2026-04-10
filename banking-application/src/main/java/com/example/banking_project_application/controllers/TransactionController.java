package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.requests.*;
import com.example.banking_project_application.dto.responses.TransactionResponse;
import com.example.banking_project_application.service.impl.AuthService;
import com.example.banking_project_application.service.impl.TransactionServiceImpl;
import com.example.banking_project_application.service.interfaces.TransactionService;
import com.example.banking_project_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing financial transactions including deposits, withdrawals, and transfers")
public class TransactionController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    private final TransactionServiceImpl txService;

    public TransactionController(TransactionServiceImpl txService) {
        this.txService = txService;
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Allows an authenticated user to deposit funds into their account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deposit successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid deposit request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized to access this account")
    })
    public ResponseEntity<TransactionResponse> deposit(@AuthenticationPrincipal UserDetails userDetails,@RequestBody DepositRequest request) {
        authService.validateAccountAccess(userDetails.getUsername(), request.getAccountId());
        return ResponseEntity.ok(txService.deposit(request));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Allows an authenticated user to withdraw funds from their account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Withdrawal successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid withdrawal request data or insufficient balance"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized to access this account")
    })
    public ResponseEntity<TransactionResponse> withdraw(@AuthenticationPrincipal UserDetails userDetails,@RequestBody WithdrawRequest request) {
        authService.validateAccountAccess(userDetails.getUsername(), request.getAccountId());
        return ResponseEntity.ok(txService.withdraw(request));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Allows an authenticated user to transfer funds from their account to another account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid transfer request data or insufficient balance"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized to access source account"),
        @ApiResponse(responseCode = "404", description = "Source or destination account not found")
    })
    public ResponseEntity<TransactionResponse> transfer(@AuthenticationPrincipal UserDetails userDetails,@RequestBody TransferRequest request) {
       authService.validateAccountAccess(userDetails.getUsername(), request.getFromAccountId());
        return ResponseEntity.ok(txService.transfer(request));
    }

    @GetMapping("/accounts/{accountId}")
    @Operation(summary = "Get account transactions", description = "Retrieves all transactions for a specific account belonging to the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized to access this account"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<List<TransactionResponse>> getTransactions(@AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Account ID", required = true, example = "5432")
            @PathVariable Long accountId) {
       authService.validateAccountAccess(userDetails.getUsername(),accountId);

        return ResponseEntity.ok(txService.getTransactionsByAccount(accountId));
    }
}