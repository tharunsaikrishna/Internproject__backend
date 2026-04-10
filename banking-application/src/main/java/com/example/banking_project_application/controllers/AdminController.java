package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.responses.*;
import com.example.banking_project_application.entity.AccountRequest;
import com.example.banking_project_application.entity.ChequeBookRequest;
import com.example.banking_project_application.entity.LoanRequest;
import com.example.banking_project_application.service.interfaces.AdminService;
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
@RequestMapping("/admin")
@Tag(name = "Admin Management", description = "APIs for admin operations including user, account, and request management")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated"),
            @ApiResponse(responseCode = "403", description = "Admin not authorized")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves details of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/block")
    @Operation(summary = "Block user", description = "Blocks a user account preventing them from accessing the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User blocked successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> blockUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        adminService.blockUser(id);
        return ResponseEntity.ok("User blocked successfully");
    }

    @PutMapping("/users/{id}/unblock")
    @Operation(summary = "Unblock user", description = "Unblocks a previously blocked user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unblocked successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> unblockUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        adminService.unblockUser(id);
        return ResponseEntity.ok("User unblocked successfully");
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get all accounts", description = "Retrieves all bank accounts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<?> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @GetMapping("/users/{id}/accounts")
    @Operation(summary = "Get user accounts", description = "Retrieves all accounts belonging to a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<?> getAccountsOfUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAccountsOfUser(id));
    }

    @PutMapping("/accounts/{id}/block")
    @Operation(summary = "Block account", description = "Blocks a bank account preventing transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account blocked successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> blockAccount(
            @Parameter(description = "Account ID", required = true, example = "5432")
            @PathVariable Long id) {
        adminService.blockAccount(id);
        return ResponseEntity.ok("Account blocked successfully");
    }

    @PutMapping("/accounts/{id}/unblock")
    @Operation(summary = "Unblock account", description = "Unblocks a previously blocked bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account unblocked successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> unblockAccount(
            @Parameter(description = "Account ID", required = true, example = "5432")
            @PathVariable Long id) {
        adminService.unblockAccount(id);
        return ResponseEntity.ok("Account unblocked successfully");
    }

    @GetMapping("/account-requests")
    @Operation(summary = "Get pending account requests", description = "Retrieves all pending account creation requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account requests retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountRequest.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<List<AccountRequestResponse>> getPendingAccountRequests() {
        return ResponseEntity.ok(adminService.getPendingAccountRequests());
    }

    @PutMapping("/account-requests/{id}/approve")
    @Operation(summary = "Approve account request", description = "Approves a pending account creation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account request approved successfully"),
            @ApiResponse(responseCode = "404", description = "Account request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> approveAccountRequest(
            @Parameter(description = "Account Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.approveAccountRequest(id, adminDetails.getUsername());
        return ResponseEntity.ok("Account request approved");
    }

    @PutMapping("/account-requests/{id}/reject")
    @Operation(summary = "Reject account request", description = "Rejects a pending account creation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account request rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Account request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> rejectAccountRequest(
            @Parameter(description = "Account Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.rejectAccountRequest(id, adminDetails.getUsername());
        return ResponseEntity.ok("Account request rejected");
    }

    @GetMapping("/loans")
    @Operation(summary = "Get pending loans", description = "Retrieves all pending loan requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending loans retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRequest.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        return ResponseEntity.ok(adminService.getPendingLoans());
    }

    @GetMapping("/loans/approved")
    @Operation(summary = "Get approved loans", description = "Retrieves all approved loan requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approved loans retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRequest.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getApprovedLoans() {
        return ResponseEntity.ok(adminService.getApprovedLoans());
    }

    @GetMapping("/loans/rejected")
    @Operation(summary = "Get rejected loans", description = "Retrieves all rejected loan requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejected loans retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRequest.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<List<LoanResponse>> getRejectedLoans() {
        return ResponseEntity.ok(adminService.getRejectedLoans());
    }

    @PutMapping("/loans/{id}/approve")
    @Operation(summary = "Approve loan", description = "Approves a pending loan request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan approved successfully"),
            @ApiResponse(responseCode = "404", description = "Loan request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> approveLoan(
            @Parameter(description = "Loan Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.approveLoan(id, adminDetails.getUsername());
        return ResponseEntity.ok("Loan approved");
    }

    @PutMapping("/loans/{id}/reject")
    @Operation(summary = "Reject loan", description = "Rejects a pending loan request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Loan request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> rejectLoan(
            @Parameter(description = "Loan Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.rejectLoan(id, adminDetails.getUsername());
        return ResponseEntity.ok("Loan rejected");
    }

    @GetMapping("/cheques")
    @Operation(summary = "Get pending cheque requests", description = "Retrieves all pending cheque book requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending cheque requests retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookRequest.class))),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<List<ChequeBookResponse>> getPendingChequeRequests() {
        return ResponseEntity.ok(adminService.getPendingChequeRequests());
    }

    @PutMapping("/cheques/{id}/approve")
    @Operation(summary = "Approve cheque request", description = "Approves a pending cheque book request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cheque request approved successfully"),
            @ApiResponse(responseCode = "404", description = "Cheque request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> approveCheque(
            @Parameter(description = "Cheque Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.approveCheque(id, adminDetails.getUsername());
        return ResponseEntity.ok("Cheque request approved");
    }

    @PutMapping("/cheques/{id}/reject")
    @Operation(summary = "Reject cheque request", description = "Rejects a pending cheque book request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cheque request rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Cheque request not found"),
            @ApiResponse(responseCode = "401", description = "Admin not authenticated")
    })
    public ResponseEntity<String> rejectCheque(
            @Parameter(description = "Cheque Request ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails adminDetails
    ) {
        adminService.rejectCheque(id, adminDetails.getUsername());
        return ResponseEntity.ok("Cheque request rejected");
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionsforAdmin(){
        return ResponseEntity.ok(adminService.getAllTransactions());
    }
}
