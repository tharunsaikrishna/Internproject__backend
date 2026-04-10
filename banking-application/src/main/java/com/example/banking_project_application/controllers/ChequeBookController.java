package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.requests.ChequeBookRequestDto;
import com.example.banking_project_application.dto.responses.ChequeBookResponse;
import com.example.banking_project_application.service.impl.AuthService;
import com.example.banking_project_application.service.interfaces.ChequeBookService;

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
@RequestMapping("/cheque")
@Tag(name = "Cheque Book Management", description = "APIs for requesting and managing cheque books")
public class ChequeBookController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChequeBookService chequeService;

    @Autowired
    private AuthService authService;

    @PostMapping("/request")
    @Operation(summary = "Request cheque book", description = "Allows an authenticated user to request a cheque book for a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cheque book request submitted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized to access this account")
    })
    public ChequeBookResponse requestChequeBook(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ChequeBookRequestDto dto
    ) {
        dto.setUserId(userService.findByUsername(userDetails.getUsername()).getUserId());
        authService.validateAccountAccess(userDetails.getUsername(), dto.getAccountId());
        return chequeService.requestChequeBook(dto);
    }

    @GetMapping("/user")
    @Operation(summary = "Get user cheque requests", description = "Retrieves all cheque book requests submitted by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User cheque book requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<ChequeBookResponse>> getUserRequests(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(chequeService.getUserChequeRequests(userId));
    }
    @GetMapping("/user/approved")
    @Operation(summary = "Get approved cheque requests", description = "Retrieves all approved cheque book requests for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Approved cheque book requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<ChequeBookResponse>> getApprovedRequests(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(chequeService.getApprovedChequeRequests(userId));
    }
    @GetMapping("/user/pending")
    @Operation(summary = "Get pending cheque requests", description = "Retrieves all pending cheque book requests for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending cheque book requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<ChequeBookResponse>> getPendingRequests(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(chequeService.getPendingChequeRequests(userId));
    }

    @GetMapping("/user/rejected")
    @Operation(summary = "Get rejected cheque requests", description = "Retrieves all rejected cheque book requests for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rejected cheque book requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChequeBookResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<List<ChequeBookResponse>> getRejectedRequests(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(chequeService.getRejectedChequeRequests(userId));
    }
}

