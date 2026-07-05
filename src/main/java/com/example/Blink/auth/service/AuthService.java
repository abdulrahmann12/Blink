package com.example.Blink.auth.service;

import com.example.Blink.auth.dto.*;
import com.example.Blink.common.dto.ChangePasswordRequest;
import com.example.Blink.common.events.CodeRegeneratedEvent;
import com.example.Blink.common.events.PasswordResetRequestedEvent;
import com.example.Blink.common.events.UserRegisteredEvent;
import com.example.Blink.exception.*;
import com.example.Blink.jwt.RefreshTokenProperties;
import com.example.Blink.jwt.entity.Token;
import com.example.Blink.jwt.repository.TokenRepository;
import com.example.Blink.jwt.service.JwtService;
import com.example.Blink.role.repository.RoleRepository;
import com.example.Blink.security.AuthenticatedUserService;
import com.example.Blink.user.dto.CreateUserRequest;
import com.example.Blink.user.dto.UserResponse;
import com.example.Blink.user.dto.VerifyAccountRequest;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.mapper.UserMapper;
import com.example.Blink.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import static com.example.Blink.config.rabbitconfig.RabbitConstants.*;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;
    private final RabbitTemplate rabbitTemplate;
    private final AuthenticatedUserService authenticatedUserService;

    private static final String DEFAULT_ROLE = "USER";

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse createUser(@Valid CreateUserRequest createUserRequest){
        if(userRepository.existsByEmail(createUserRequest.getEmail())){
            throw new EmailAlreadyExistsException();
        }
        if(userRepository.existsByUsername(createUserRequest.getUsername())){
            throw new UsernameAlreadyExistsException();
        }
        User user = userMapper.toEntity(createUserRequest);
        user.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setRole(roleRepository.findByRoleName(DEFAULT_ROLE).orElseThrow(RoleNotFoundException::new));
        user.setEmail(createUserRequest.getEmail().trim().toLowerCase());
        user.setUsername(createUserRequest.getUsername().trim().toLowerCase());
        user.setActive(false);
        user.setVerificationCode(generateConfirmationCode());
        User savedUser = userRepository.save(user);

        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getVerificationCode(),
                Instant.now()
        );
        rabbitTemplate.convertAndSend(AUTH_EXCHANGE, USER_REGISTERED_KEY, userRegisteredEvent);
        return userMapper.toResponse(savedUser);
    }


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

    @Transactional
    public void verifyAccount(@Valid VerifyAccountRequest verifyAccountRequest){
        User user = userRepository.findByUsernameOrEmailWithRole(verifyAccountRequest.getUsernameOrEmail())
                .orElseThrow(UserNotFoundException::new);

        if (user.isActive()) {
            throw new AccountAlreadyVerifiedException();
        }
        if(!user.getVerificationCode().equals(verifyAccountRequest.getVerificationCode())){
            throw new InvalidVerificationCodeException();
        }
        user.setActive(true);
        user.setVerificationCode(null);

        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                null,
                Instant.now()
        );
        rabbitTemplate.convertAndSend(AUTH_EXCHANGE,USER_EMAIL_VERIFIED_KEY,userRegisteredEvent);
    }

    @Transactional
    public void reGenerateCode(@Valid EmailRequestDTO emailRequestDTO){
        User user = userRepository.findByUsernameOrEmailWithRole(emailRequestDTO.getUsernameOrEmail())
                .orElseThrow(UserNotFoundException::new);
        if (user.getVerificationCodeExpiresAt() != null &&
                Instant.now().isBefore(user.getVerificationCodeExpiresAt())) {

            throw new VerificationCodeAlreadySentException();
        }
        String newCode = generateConfirmationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(5 * 60)); // Code valid for 5 minutes
        CodeRegeneratedEvent codeRegeneratedEvent = new CodeRegeneratedEvent(
                user.getEmail(),
                user.getUsername(),
                newCode,
                Instant.now()
        );
        rabbitTemplate.convertAndSend(AUTH_EXCHANGE, CODE_REGENERATED_KEY, codeRegeneratedEvent);
    }

    @Transactional
    public void forgetPassword(@Valid EmailRequestDTO emailRequestDTO){
        User user = userRepository.findByUsernameOrEmailWithRole(emailRequestDTO.getUsernameOrEmail())
                .orElseThrow(UserNotFoundException::new);
        if(!user.isActive()){
            throw new UserNotActiveException();
        }
        String newCode = generateConfirmationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(5 * 60)); // Code valid for 5 minutes
        PasswordResetRequestedEvent passwordResetRequestedEvent = new PasswordResetRequestedEvent(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                newCode,
                Instant.now()
        );
        rabbitTemplate.convertAndSend(AUTH_EXCHANGE, PASSWORD_RESET_KEY, passwordResetRequestedEvent);
    }

    @Transactional
    public void changePassword(@Valid ChangePasswordRequest changePasswordRequestDTO){

        User user = authenticatedUserService.getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequestDTO.getCurrentPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException();
        }
        user.setPasswordHash(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
    }

    @Transactional
    public void resetPassword(@Valid ResetPasswordRequestDTO resetPasswordRequestDTO){
        User user = userRepository.findByUsernameOrEmailWithRole(resetPasswordRequestDTO.getUsernameOrEmail())
                .orElseThrow(UserNotFoundException::new);
        String verificationCode = user.getVerificationCode();

        if(user.getVerificationCodeExpiresAt() == null || Instant.now().isAfter(user.getVerificationCodeExpiresAt())) {
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            throw new VerificationCodeExpiredException();
        }

        if (verificationCode == null || !verificationCode.equals(resetPasswordRequestDTO.getCode())) {
            throw new InvalidVerificationCodeException();
        }
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
    }

    public String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
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
