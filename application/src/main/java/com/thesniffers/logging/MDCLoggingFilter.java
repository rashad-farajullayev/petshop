package com.thesniffers.logging;

import com.thesniffers.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2)
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Fetch Security Context values
            String currentUserToken = SecurityUtils.getCurrentUserToken();
            boolean isAdmin = SecurityUtils.isAdmin();

            // Store values in MDC
            MDC.put("CURRENT_USER_TOKEN", currentUserToken);
            MDC.put("IS_ADMIN", String.valueOf(isAdmin));

            filterChain.doFilter(request, response); // Continue request processing
        } finally {
            MDC.clear(); // Ensure MDC is cleaned up after request
        }
    }
}
