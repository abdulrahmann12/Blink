package com.example.Blink.exception;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
@ControllerAdvice
public class GlobalExceptionHandler {
    // === Common Utility === //
    private ResponseEntity<BaseResponse> buildErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
         BaseResponse response = new  BaseResponse(  ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, status);
    }

    private ResponseEntity< BaseResponse> buildErrorResponse(String message, WebRequest request, HttpStatus status) {
         BaseResponse response = new  BaseResponse(  message, request.getDescription(false));
        return new ResponseEntity<>( response, status);
    }

    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<BaseResponse> handleMailException(MailSendingException ex, HttpServletRequest request) {
        BaseResponse response = new BaseResponse(ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(QrGenerationException.class)
    public ResponseEntity<BaseResponse> handleQrCodeException(QrGenerationException ex, HttpServletRequest request) {
        BaseResponse response = new BaseResponse(ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<BaseResponse> handleImageUpload(ImageUploadException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ImageDeletedException.class)
    public ResponseEntity<BaseResponse> handleImageDeleted(ImageDeletedException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ImageNullException.class)
    public ResponseEntity<BaseResponse> handleImageNull(ImageNullException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            AuthenticationCredentialsNotFoundException.class,
            BadCredentialsException.class,
            AuthenticationException.class
    })
    public ResponseEntity< BaseResponse> handleAuthenticationExceptions(Exception ex, WebRequest request) {
        String message = (ex instanceof BadCredentialsException) ? Messages.BAD_CREDENTIALS :
                (ex instanceof AuthenticationException) ? Messages.AUTH_FAILED :
                        ex.getMessage();
        return buildErrorResponse(message, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity< BaseResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(Messages.ACCESS_DENIED, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity< BaseResponse> handleJwtExpired(ExpiredJwtException ex, WebRequest request) {
        return buildErrorResponse(Messages.SESSION_EXPIRED, request, HttpStatus.BAD_REQUEST);
    }


    // === Business Exceptions === //

    @ExceptionHandler(AliasAlreadyUsed.class)
    public ResponseEntity<BaseResponse> handleAliasAlreadyUsed(AliasAlreadyUsed ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            UrlNotFoundException.class,
            UrlNotActiveException.class,
            UrlExpiredException.class,
            RoleNotFoundException.class,
            UserNotFoundException.class,
            UserNotActiveException.class,
            QrCodeNotFoundException.class,

    })
    public ResponseEntity<BaseResponse> handleNotFoundBusinessExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            RoleAlreadyExistsException.class,
            UserAlreadyDeactivatedException.class,
            UserAlreadyActivatedException.class,
            QrCodeAlreadyExistsException.class,

    })
    public ResponseEntity<BaseResponse> handleConflictExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UrlLockedException.class)
    public ResponseEntity<BaseResponse> handleUrlLocked(UrlLockedException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.LOCKED);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<BaseResponse> handleWrongPassword(WrongPasswordException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<BaseResponse> handleInvalidToken(InvalidTokenException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidNewPasswordException.class)
    public ResponseEntity<BaseResponse> handleInvalidPassword(InvalidNewPasswordException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyLoggedOutException.class)
    public ResponseEntity<BaseResponse> handleAlreadyLoggedOut(AlreadyLoggedOutException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity< BaseResponse> handleUnauthorizedAction(UnauthorizedActionException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity< BaseResponse> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }

    // === Validation Exceptions === //

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity< BaseResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildErrorResponse(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<BaseResponse> handleInvalidUrl(InvalidUrlException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity< BaseResponse> handleSpringJsonParseException(
            HttpMessageNotReadableException ex) {
         BaseResponse error = new BaseResponse(Messages.INVALID_DATA);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity< BaseResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildErrorResponse(Messages.REQUEST_NOT_SUPPORTED, request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity< BaseResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String firstError = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Validation failed");
        return buildErrorResponse(firstError, request, HttpStatus.BAD_REQUEST);

    }

    // === Fallback Exceptions === //

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity< BaseResponse> handleRuntime(RuntimeException ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred. Please try again later.", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity< BaseResponse> handleAll(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred. Please try again later.", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
