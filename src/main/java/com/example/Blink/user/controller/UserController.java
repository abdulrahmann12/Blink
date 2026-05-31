package com.example.Blink.user.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.user.dto.CreateUserRequest;
import com.example.Blink.user.dto.UpdateUserRequest;
import com.example.Blink.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = SwaggerMessages.TAG_USER, description = SwaggerMessages.TAG_USER_DESC)
public class UserController {
    private final UserService userService;

    @Operation(summary = SwaggerMessages.CREATE_USER, description = SwaggerMessages.CREATE_USER_DESC)
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.USER_CREATED, userService.createUser(request)));
    }

    @Operation(summary = SwaggerMessages.UPDATE_USER, description = SwaggerMessages.UPDATE_USER_DESC)
    @PutMapping("/{userId}")
    public ResponseEntity<BaseResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(new BaseResponse(Messages.USER_UPDATED, userService.updateUser(userId, request)));
    }

    @Operation(summary = SwaggerMessages.GET_USER_BY_ID, description = SwaggerMessages.GET_USER_BY_ID_DESC)
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(new BaseResponse(Messages.USER_FETCHED, userService.getUserById(userId)));
    }

    @Operation(summary = SwaggerMessages.GET_USER_BY_IDENTIFIER, description = SwaggerMessages.GET_USER_BY_IDENTIFIER_DESC)
    @GetMapping("/find/{identifier}")
    public ResponseEntity<BaseResponse> getUserByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(new BaseResponse(Messages.USER_FETCHED, userService.getUserByUsernameOrEmail(identifier)));
    }

    @Operation(summary = SwaggerMessages.GET_ALL_USERS, description = SwaggerMessages.GET_ALL_USERS_DESC)
    @GetMapping
    public ResponseEntity<BaseResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.USERS_FETCHED, userService.findAllUsers(page, size)));
    }

    @Operation(summary = SwaggerMessages.GET_DEACTIVATED_USERS, description = SwaggerMessages.GET_DEACTIVATED_USERS_DESC)
    @GetMapping("/deactivated")
    public ResponseEntity<BaseResponse> getAllDeactivatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.USERS_FETCHED, userService.findAllDeactivatedUsers(page, size)));
    }

    @Operation(summary = SwaggerMessages.DELETE_USER, description = SwaggerMessages.DELETE_USER_DESC)
    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new BaseResponse(Messages.USER_DELETED));
    }

    @Operation(summary = SwaggerMessages.ACTIVATE_USER, description = SwaggerMessages.ACTIVATE_USER_DESC)
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<BaseResponse> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(new BaseResponse(Messages.USER_ACTIVATED));
    }

    @Operation(summary = SwaggerMessages.SEARCH_USERS, description = SwaggerMessages.SEARCH_USERS_DESC)
    @GetMapping("/search")
    public ResponseEntity<BaseResponse> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.USERS_FETCHED, userService.searchUsers(keyword, page, size)));
    }

    @Operation(summary = SwaggerMessages.GET_USERS_BY_ROLE, description = SwaggerMessages.GET_USERS_BY_ROLE_DESC)
    @GetMapping("/role/{roleId}")
    public ResponseEntity<BaseResponse> getUsersByRole(
            @PathVariable Long roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.USERS_FETCHED, userService.getUsersByRole(roleId, page, size)));
    }

    @Operation(summary = SwaggerMessages.UPDATE_PROFILE_PICTURE, description = SwaggerMessages.UPDATE_PROFILE_PICTURE_DESC)
    @PatchMapping("/{userId}/profile-picture")
    public ResponseEntity<BaseResponse> updateProfilePicture(
            @PathVariable Long userId,
            @RequestPart MultipartFile image) throws IOException {
        return ResponseEntity.ok(new BaseResponse(Messages.PROFILE_PICTURE_UPDATED, userService.updateProfilePicture(userId, image)));
    }
}
