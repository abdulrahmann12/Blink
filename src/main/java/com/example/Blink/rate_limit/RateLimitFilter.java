package com.example.Blink.rate_limit;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.exception.RateLimitExceededException;
import com.example.Blink.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final RateLimitConfig rateLimitConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ruleName = resolveRule(request);

        if (ruleName != null) {

            RateLimitRule rule = rateLimitConfig.getRule(ruleName);

            String key = resolveKey(request);

            boolean allowed =
                    rateLimitService.tryConsume(key + "_" + ruleName, rule);

            if (!allowed) {
                sendRateLimitResponse(response, request);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveRule(HttpServletRequest request) {

        String path = request.getServletPath();
        String method = request.getMethod();

        if (path.equals("/api/v1/auth/login")
                && method.equals("POST")) {
            return "LOGIN";
        }

        if (path.equals("/api/v1/auth/register")
                && method.equals("POST")) {
            return "REGISTER";
        }

        if (path.equals("/api/v1/auth/regenerate-code")
                && method.equals("POST")) {
            return "REGENERATE";
        }

        if (path.equals("/api/v1/auth/forget-password")
                && method.equals("POST")) {
            return "RESET-PASSWORD";
        }

        if (path.equals("/api/v1/urls")
                && method.equals("POST")) {
            return "CREATE_URL";
        }

        if (path.equals("/api/v1/urls/dashboard")
                && method.equals("GET")) {
            return "DASHBOARD";
        }

        if (path.startsWith("/api/v1/qr-codes/generate")
                && method.equals("POST")) {
            return "QR_GENERATE";
        }

        if (path.endsWith("/stats")
                && method.equals("GET")) {
            return "STATS";
        }

        return null;
    }

    private String resolveKey(HttpServletRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            return "USER_" + userDetails.getUserId();
        }

        return "IP_" + request.getRemoteAddr();
    }

    private void sendRateLimitResponse(
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {

        BaseResponse error =
                new BaseResponse(
                        Messages.TOO_MANY_REQUESTS,
                        request.getRequestURI()
                );

        response.setStatus(429);
        response.setContentType("application/json");

        new ObjectMapper().writeValue(
                response.getWriter(),
                error
        );
    }
}