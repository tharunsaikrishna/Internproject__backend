package com.example.banking_project_application.service.interfaces;


import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.requests.UserRegisterRequest;
import com.example.banking_project_application.dto.responses.UserResponse;
import com.example.banking_project_application.entity.Admin;
import com.example.banking_project_application.entity.User;

public interface UserService {


    User register(UserRegisterRequest request);
    User validateLogin(String username, String password);
    User findByUsername(String username);
    String login(UserLoginRequest request);
    UserResponse getUserProfile(Long id);
    UserResponse updateProfile(Long id, UserResponse update);

    void changePassword(Long userId, String oldPwd, String newPwd);

}

