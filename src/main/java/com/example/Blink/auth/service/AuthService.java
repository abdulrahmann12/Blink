package com.example.Blink.auth.service;

import com.example.Blink.auth.dto.AuthResponse;
import com.example.Blink.auth.dto.LoginRequestDTO;
import com.example.Blink.auth.dto.RefreshTokenRequest;
import com.example.Blink.exception.AlreadyLoggedOutException;
import com.example.Blink.exception.InvalidTokenException;
import com.example.Blink.exception.UserNotActiveException;
import com.example.Blink.exception.UserNotFoundException;
import com.example.Blink.exception.WrongPasswordException;
import com.example.Blink.jwt.RefreshTokenProperties;
import com.example.Blink.jwt.entity.Token;
import com.example.Blink.jwt.repository.TokenRepository;
import com.example.Blink.jwt.service.JwtService;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;

    @Transactional
    public AuthResponse login(@Valid LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsernameOrEmailWithRole(loginRequestDTO.getUsernameOrEmail())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException();
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = generateRefreshToken();
        String hashedRefresh = hashToken(refreshToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(refreshTokenProperties.getExpirationMinutes());

        tokenRepository.revokeAllRefreshTokensByUser(user.getUserId());

        tokenRepository.save(Token.builder()
                .user(user)
                .token(hashedRefresh)
                .expired(false)
                .revoked(false)
                .expiresAt(expiresAt)
                .build());

        return new AuthResponse(accessToken, refreshToken);
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Transactional
    public AuthResponse refreshToken(@Valid RefreshTokenRequest request) {
        String hashed = hashToken(request.getRefreshToken());
        Token token = tokenRepository.findByTokenWithUser(hashed)
                .orElseThrow(InvalidTokenException::new);

        if (token.isExpired() || token.isRevoked()) {
            throw new InvalidTokenException();
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token.setExpired(true);
            tokenRepository.save(token);
            throw new InvalidTokenException();
        }

        User user = token.getUser();
        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = generateRefreshToken();
        String hashedNew = hashToken(newRefreshToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(refreshTokenProperties.getExpirationMinutes());

        // Rotate refresh token — revoke old, save new
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);

        tokenRepository.save(Token.builder()
                .user(user)
                .token(hashedNew)
                .expired(false)
                .revoked(false)
                .expiresAt(expiresAt)
                .build());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String hashed = hashToken(request.getRefreshToken());
        Token token = tokenRepository.findByTokenWithUser(hashed)
                .orElseThrow(InvalidTokenException::new);
        if (token.isRevoked() || token.isExpired()) {
            throw new AlreadyLoggedOutException();
        }
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }
}
