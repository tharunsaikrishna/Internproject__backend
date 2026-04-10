package com.example.banking_project_application.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.banking_project_application.config.JwtTokenProvider;
import com.example.banking_project_application.dto.requests.UserLoginRequest;
import com.example.banking_project_application.dto.requests.UserRegisterRequest;
import com.example.banking_project_application.dto.responses.UserResponse;
import com.example.banking_project_application.entity.Admin;
import com.example.banking_project_application.entity.User;
import com.example.banking_project_application.exceptions.InvalidRequestException;
import com.example.banking_project_application.exceptions.UnauthorizedException;
import com.example.banking_project_application.repositories.AdminRepository;
import com.example.banking_project_application.repositories.UserRepository;
import com.example.banking_project_application.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtProvider;


    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(UserRegisterRequest req) {
        log.info("Register attempt: username={}", req.getUsername());
        if (userRepository.existsByUsername(req.getUsername())) {
            log.warn("Registration failed - Username '{}' already exists", req.getUsername());
            throw new InvalidRequestException("Username already exists.");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setStatus(User.Status.ACTIVE);
        User saved = userRepository.save(user);
        log.info("User registered successfully: username={} id={}", saved.getUsername(), saved.getUserId());
        return saved;
    }


    // -------------------- LOGIN USER --------------------
    @Override
    public User validateLogin(String username, String password) {
        log.info("Login validation attempt for username='{}'", username);
        User user = userRepository.findByUsername(username)

                .orElseThrow(() -> {
                    log.warn("Login failed - Username '{}' not found", username);
                    return new UnauthorizedException("Invalid username or password.");
                });


        if (!passwordEncoder.matches(password, user.getPassword())) {

            log.warn("Login failed - Incorrect password for username='{}'", username);
            throw new UnauthorizedException("Invalid username or password.");
        }
        if (user.getStatus() == User.Status.BLOCKED) {
            log.warn("Login failed - User '{}' is BLOCKED", username);
            throw new UnauthorizedException("User account is blocked.");
        }

        log.info("Login validation SUCCESS for username='{}'", username);
        return user;
    }



    @Override
    public String login(UserLoginRequest request) {
        log.info("Login attempt: username='{}'", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())

        .orElseThrow(() -> {
                    log.error("Login failed - User '{}' not found", request.getUsername());
                    return new RuntimeException("User not found");
                });


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - Wrong password for username='{}'", request.getUsername());
            throw new RuntimeException("Invalid password");
        }

        log.info("Login SUCCESS for username='{}' (JWT issued)", request.getUsername());

        return jwtProvider.generateToken(user.getUsername(), "USER");
    }



    @Override
    public UserResponse getUserProfile(Long id) {
        log.info("Fetching profile for userId={}", id);
        User user = userRepository.findById(id).orElseThrow(
                () -> {
                    log.error("User profile fetch FAILED - userId={} not found", id);
                    return new InvalidRequestException("User not found");
                }
        );
        log.info("Profile fetched for username='{}' id={}", user.getUsername(), id);
        return toResponse(user);
    }

    @Override
    public UserResponse updateProfile(Long id, UserResponse req) {

        log.info("Updating profile for userId={}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Profile update FAILED - userId={} not found", id);
            return new InvalidRequestException("User not found");
        });

        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());

        log.info("Profile updated successfully for username='{}'", user.getUsername());

        userRepository.save(user);
        return toResponse(user);
    }

    @Override
    public void changePassword(Long id, String oldPwd, String newPwd) {
        log.info("Password change attempt: userId={}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Password change FAILED - userId={} not found", id);
            return new InvalidRequestException("User not found");
        });

        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            log.warn("Password change failed - Incorrect old password for username='{}'", user.getUsername());
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPwd));
        userRepository.save(user);
        log.info("Password changed successfully for username='{}'", user.getUsername());
    }


    @Override
    public User findByUsername(String username) {
        log.debug("Fetching user by username='{}'", username);
         return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User fetch FAILED - username='{}' not found", username);
                    return new InvalidRequestException("User not found");
                });

    }

    private UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getUserId());
        r.setUsername(user.getUsername());
        r.setFullName(user.getFullName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setAddress(user.getAddress());
        r.setStatus(user.getStatus().name());

        return r;
    }
}

