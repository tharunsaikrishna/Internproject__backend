package com.example.banking_project_application.controllers;

import com.example.banking_project_application.dto.responses.UserResponse;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User Profile Management", description = "APIs for managing user profile information and account settings")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the profile information of the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());
        UserResponse response = userService.getUserProfile(user.getUserId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-profile")
    @Operation(summary = "Update user profile", description = "Updates the profile information of the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserResponse request) {

        User user = userService.findByUsername(userDetails.getUsername());
        UserResponse updated = userService.updateProfile(user.getUserId(), request);

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the password for the authenticated user's account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Password updated successfully"))),
        @ApiResponse(responseCode = "400", description = "Invalid password or old password incorrect"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPwd,
            @RequestParam String newPwd) {

        User user = userService.findByUsername(userDetails.getUsername());
        userService.changePassword(user.getUserId(), oldPwd, newPwd);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Password updated successfully");
    }
}