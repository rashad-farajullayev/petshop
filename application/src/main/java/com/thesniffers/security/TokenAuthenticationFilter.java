package com.thesniffers.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
@Order(1)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final String adminToken;
    private final String tenant1Token;
    private final String tenant2Token;

    public TokenAuthenticationFilter(
            @Value("${security.admin.token}") String adminToken,
            @Value("${security.tenant1.token}") String tenant1Token,
            @Value("${security.tenant2.token}") String tenant2Token) {
        this.adminToken = adminToken;
        this.tenant1Token = tenant1Token;
        this.tenant2Token = tenant2Token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String role;

        if (token.equals(adminToken)) {
            role = "ROLE_ADMIN";
        } else if (token.equals(tenant1Token) || token.equals(tenant2Token)) {
            role = "ROLE_TENANT";
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(token, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
