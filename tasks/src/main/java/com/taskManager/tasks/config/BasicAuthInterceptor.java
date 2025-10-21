package com.taskManager.tasks.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class BasicAuthInterceptor implements HandlerInterceptor {
    
    @Value("${app.basic.auth.username}")
    private String username;
    
    @Value("${app.basic.auth.password}")
    private String password;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Allow OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            log.warn("Missing or invalid Authorization header");
            sendUnauthorizedResponse(response);
            return false;
        }
        
        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
            
            String[] values = credentials.split(":", 2);
            
            if (values.length != 2) {
                log.warn("Invalid credentials format");
                sendUnauthorizedResponse(response);
                return false;
            }
            
            String providedUsername = values[0];
            String providedPassword = values[1];
            
            if (username.equals(providedUsername) && password.equals(providedPassword)) {
                return true;
            } else {
                log.warn("Invalid credentials provided");
                sendUnauthorizedResponse(response);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error during authentication", e);
            sendUnauthorizedResponse(response);
            return false;
        }
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized access. Please provide valid credentials.\"}");
    }
}
