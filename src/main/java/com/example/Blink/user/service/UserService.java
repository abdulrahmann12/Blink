package com.example.Blink.user.service;

import com.example.Blink.common.service.ImageService;
import com.example.Blink.exception.EmailAlreadyExistsException;
import com.example.Blink.exception.RoleNotFoundException;
import com.example.Blink.exception.ImageNullException;
import com.example.Blink.exception.UserNotActiveException;
import com.example.Blink.exception.UserAlreadyDeactivatedException;
import com.example.Blink.exception.UserNotFoundException;
import com.example.Blink.exception.UsernameAlreadyExistsException;
import com.example.Blink.common.dto.ImageUploadResult;
import com.example.Blink.role.repository.RoleRepository;
import com.example.Blink.role.entity.Role;
import com.example.Blink.user.dto.UpdateUserRequest;
import com.example.Blink.user.dto.UserResponse;
import com.example.Blink.user.dto.UserSummaryResponse;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.mapper.UserMapper;
import com.example.Blink.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final ImageService imageService;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateUser(Long userId, @Valid UpdateUserRequest updateUserRequest){
        User user = userRepository.findByIdWithRole(userId).orElseThrow(UserNotFoundException::new);
        if (!user.isActive()) {
            throw new UserNotActiveException();
        }
        if(userRepository.existsByEmail(updateUserRequest.getEmail()) && !user.getEmail().equals(updateUserRequest.getEmail().trim().toLowerCase())){
            throw new EmailAlreadyExistsException();
        }
        if(userRepository.existsByUsername(updateUserRequest.getUsername()) && !user.getUsername().equals(updateUserRequest.getUsername().trim().toLowerCase())){
            throw new UsernameAlreadyExistsException();
        }
        user.setEmail(updateUserRequest.getEmail().trim().toLowerCase());
        user.setUsername(updateUserRequest.getUsername().trim().toLowerCase());
        user.setFullName(updateUserRequest.getFullName());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Cacheable(value = "users", key = "#p0")
    public UserResponse getUserById(Long userId){
        User user = userRepository.findByIdWithRole(userId).orElseThrow(UserNotFoundException::new);
        return userMapper.toResponse(user);
    }

    @Cacheable(value = "users", key = "#p0")
    public UserResponse getUserByUsernameOrEmail(String emailOrUsername){
        User user = userRepository.findByUsernameOrEmailWithRole(emailOrUsername.trim().toLowerCase())
                .orElseThrow(UserNotFoundException::new);
        return userMapper.toResponse(user);
    }

    public Page<UserSummaryResponse> findAllUsers(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllActive(pageable).map(userMapper::toSummaryResponse);
    }

    public Page<UserSummaryResponse> findAllDeactivatedUsers(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllDeactivated(pageable).map(userMapper::toSummaryResponse);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(!user.isActive()){
            throw new UserAlreadyDeactivatedException();
        }
        user.setActive(false);
        user.setDeletesAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setActive(true);
        user.setDeletesAt(null);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateProfilePicture(Long userId, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new ImageNullException();
        }
        User user = userRepository.findByIdWithRole(userId).orElseThrow(UserNotFoundException::new);
        ImageUploadResult imageUploadResult = imageService.uploadImage(image.getBytes());
        user.setProfilePictureUrl(imageUploadResult.imageUrl());
        return userMapper.toResponse(userRepository.save(user));
    }

    public Page<UserSummaryResponse> getUsersByRole(Long roleId, int page, int size) {
        Role role = roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole(role, pageable).map(userMapper::toSummaryResponse);
    }

    public Page<UserSummaryResponse> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(keyword.trim(), pageable).map(userMapper::toSummaryResponse);
    }
}
