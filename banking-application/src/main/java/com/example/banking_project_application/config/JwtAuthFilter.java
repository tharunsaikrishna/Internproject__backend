package com.example.banking_project_application.config;

import com.example.banking_project_application.service.impl.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtTokenProvider jwtProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("No JWT token found for path={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (tokenBlacklistService.isBlacklisted(token)) {
            log.warn("Blacklisted JWT used at path={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtProvider.validateToken(token)) {
            log.warn("Invalid/Expired JWT at path={} token={}", request.getRequestURI(), token);
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtProvider.getUsernameFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        log.info("JWT validated: user='{}', role='{}', path={}", username, role, request.getRequestURI());

        UserDetails userDetails = User.withUsername(username)
                .password("")
                .authorities("ROLE_" + role)
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}