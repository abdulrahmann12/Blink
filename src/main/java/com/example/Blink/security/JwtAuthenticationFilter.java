package com.example.Blink.security;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            String username = jwtService.extractUsername(token);

            if (username == null) {
                throw new AuthenticationCredentialsNotFoundException(Messages.BAD_CREDENTIALS);
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    // Use authorities from CustomUserDetails — already has ROLE_ prefix
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (AuthenticationCredentialsNotFoundException ex) {
            sendErrorResponse(response, Messages.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED, request);
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            sendErrorResponse(response, Messages.SESSION_EXPIRED, HttpServletResponse.SC_UNAUTHORIZED, request);
        } catch (io.jsonwebtoken.JwtException ex) {
            sendErrorResponse(response, Messages.BAD_CREDENTIALS, HttpServletResponse.SC_UNAUTHORIZED, request);
        } catch (Exception ex) {
            sendErrorResponse(response, Messages.AUTH_FAILED, HttpServletResponse.SC_UNAUTHORIZED, request);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationCredentialsNotFoundException(Messages.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if (token.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException(Messages.UNAUTHORIZED);
        }
        return token;
    }

    private void sendErrorResponse(HttpServletResponse response, String message,
                                   int status, HttpServletRequest request) throws IOException {
        BaseResponse error = new BaseResponse(message, request.getRequestURI());
        response.setStatus(status);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), error);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/verify-account")
                || path.equals("/api/v1/auth/register")
                || path.equals("/api/v1/auth/refresh-token")
                || path.equals("/api/v1/auth/reset-password")
                || path.equals("/api/v1/auth/regenerate-code")
                || path.equals("/api/v1/auth/forget-password")
                || (path.matches("/api/v1/urls/[A-Za-z0-9]{7}") && "GET".equals(request.getMethod()))
                || (path.matches("/api/v1/urls/[A-Za-z0-9]{7}/unlock") && "POST".equals(request.getMethod()))
                || (path.equals("/api/v1/urls/check") && "GET".equals(request.getMethod()));         // GET /check
    }
}

