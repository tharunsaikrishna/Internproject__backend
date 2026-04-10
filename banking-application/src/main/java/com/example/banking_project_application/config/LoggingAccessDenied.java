package com.example.banking_project_application.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class LoggingAccessDenied implements AccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(LoggingAccessDenied.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("403 Forbidden: method={} path={} reason={}",
                request.getMethod(), request.getRequestURI(),
                safe(accessDeniedException != null ? accessDeniedException.getMessage() : "N/A"));

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access is denied\"}");
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("\\s+", " ").trim();
    }
}
