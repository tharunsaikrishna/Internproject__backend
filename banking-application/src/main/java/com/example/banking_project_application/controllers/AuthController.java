package com.example.banking_project_application.controllers;

import com.example.banking_project_application.config.HttpLoggingFilter;
import com.example.banking_project_application.config.JwtTokenProvider;
import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.requests.UserRegisterRequest;
import com.example.banking_project_application.entity.Admin;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.service.impl.TokenBlacklistService;
import com.example.banking_project_application.service.interfaces.AdminService;
import com.example.banking_project_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user and admin authentication and registration")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account with the provided registration details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"User registered successfully with ID: 1\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or user already exists"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok("User registered successfully with ID: " + user.getUserId());
    }

    @PostMapping("/login/user")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token for authenticated requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "object", 
                example = "{\"jwtToken\": \"eyJhbGciOiJIUzUxMiJ9...\", \"userId\": 1, \"role\": \"USER\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Authentication failed - invalid username or password")
    })
    public ResponseEntity<?> userLogin(@RequestBody UserLoginRequest request) {
        User user = userService.validateLogin(request.getUsername(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getUsername(), "USER");

        return ResponseEntity.ok(
                new Object() {
                    public final String jwtToken = token;
                    public final Long userId = user.getUserId();
                    public final String role = "USER";
                }
        );
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Admin login", description = "Authenticates an admin and returns a JWT token with admin privileges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "object", 
                example = "{\"jwtToken\": \"eyJhbGciOiJIUzUxMiJ9...\", \"adminId\": 1, \"role\": \"ADMIN\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Authentication failed - invalid username or password")
    })
    public ResponseEntity<?> adminLogin(@RequestBody UserLoginRequest request) {
        Admin admin = adminService.validateAdminLogin(request.getUsername(), request.getPassword());
        String token = jwtTokenProvider.generateToken(admin.getUsername(), "ADMIN");

        return ResponseEntity.ok(
                new Object() {
                    public final String jwtToken = token;
                    public final Long adminId = admin.getAdminId();
                    public final String role = "ADMIN";
                }
        );
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register new admin", description = "Creates a new admin account with the provided registration details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"Admin registered successfully with ID: 1\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or admin already exists"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> registerAdmin(@RequestBody UserLoginRequest request) {
        Admin user = adminService.register(request);
        return ResponseEntity.ok("Admin registered successfully with ID: " + user.getAdminId());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return ResponseEntity.ok("Logged out successfully");
    }
}
