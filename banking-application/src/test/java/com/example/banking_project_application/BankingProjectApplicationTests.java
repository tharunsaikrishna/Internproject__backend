package com.example.banking_project_application;

import com.example.banking_project_application.controllers.AuthController;
import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.requests.UserRegisterRequest;
import com.example.banking_project_application.entity.Admin;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.UnauthorizedException;
import com.example.banking_project_application.service.interfaces.AdminService;
import com.example.banking_project_application.service.interfaces.UserService;
import com.example.banking_project_application.config.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BankingProjectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Mock
    private UserService userService;

    @Mock
    private AdminService adminService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
    }


    @Test
    void testRegisterUser_Positive() {
        UserRegisterRequest req = new UserRegisterRequest(
                "john", "John Doe", "john@mail.com", "98765", "BLR", "pwd"
        );

        User mockUser = new User();
        mockUser.setUserId(1L);

        when(userService.register(req)).thenReturn(mockUser);

        ResponseEntity<?> response = authController.registerUser(req);

        assertEquals("User registered successfully with ID: 1", response.getBody());
        verify(userService, times(1)).register(req);
    }

    @Test
    void testRegisterUser_Negative_UsernameAlreadyExists() {
        UserRegisterRequest req = new UserRegisterRequest(
                "john", "John Doe", "email", "9999", "blr", "pwd"
        );

        when(userService.register(req))
                .thenThrow(new InvalidRequestException("Username already exists."));

        assertThrows(InvalidRequestException.class, () -> authController.registerUser(req));
    }

    @Test
    void testUserLogin_Positive() throws Exception {
        UserLoginRequest req = new UserLoginRequest("john", "pwd");

        User u = new User();
        u.setUserId(10L);
        u.setUsername("john");

        when(userService.validateLogin("john", "pwd")).thenReturn(u);
        when(jwtTokenProvider.generateToken("john", "USER")).thenReturn("FAKE_USER_TOKEN");

        ResponseEntity<?> result = authController.userLogin(req);

        String json = mapper.writeValueAsString(result.getBody());

        assertTrue(json.contains("FAKE_USER_TOKEN"));
        verify(userService).validateLogin("john", "pwd");
    }

    @Test
    void testUserLogin_Negative_WrongPassword() {
        UserLoginRequest req = new UserLoginRequest("john", "wrong");

        when(userService.validateLogin("john", "wrong"))
                .thenThrow(new UnauthorizedException("Invalid username or password."));

        assertThrows(UnauthorizedException.class, () -> authController.userLogin(req));
    }

    @Test
    void testUserLogin_Negative_UserNotFound() {
        UserLoginRequest req = new UserLoginRequest("unknown", "pwd");

        when(userService.validateLogin("unknown", "pwd"))
                .thenThrow(new UnauthorizedException("Invalid username or password."));

        assertThrows(UnauthorizedException.class, () -> authController.userLogin(req));
    }

    @Test
    void testAdminLogin_Positive() throws Exception {
        UserLoginRequest req = new UserLoginRequest("admin", "pwd");

        Admin admin = new Admin();
        admin.setAdminId(5L);
        admin.setUsername("admin");

        when(adminService.validateAdminLogin("admin", "pwd")).thenReturn(admin);
        when(jwtTokenProvider.generateToken("admin", "ADMIN")).thenReturn("ADMIN_TOKEN");

        ResponseEntity<?> response = authController.adminLogin(req);

        String json = mapper.writeValueAsString(response.getBody());

        assertTrue(json.contains("ADMIN_TOKEN"));
        verify(adminService).validateAdminLogin("admin", "pwd");
    }

    @Test
    void testAdminLogin_Negative_WrongPassword() {
        UserLoginRequest req = new UserLoginRequest("admin", "wrong");

        when(adminService.validateAdminLogin("admin", "wrong"))
                .thenThrow(new UnauthorizedException("Invalid admin credentials."));

        assertThrows(UnauthorizedException.class, () -> authController.adminLogin(req));
    }

    @Test
    void testAdminLogin_Negative_AdminNotFound() {
        UserLoginRequest req = new UserLoginRequest("ghost", "pwd");

        when(adminService.validateAdminLogin("ghost", "pwd"))
                .thenThrow(new UnauthorizedException("Invalid admin credentials."));

        assertThrows(UnauthorizedException.class, () -> authController.adminLogin(req));
    }
}