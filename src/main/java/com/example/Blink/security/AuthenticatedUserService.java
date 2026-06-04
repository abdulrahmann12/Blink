package com.example.Blink.security;

import com.example.Blink.exception.UnauthorizedException;
import com.example.Blink.exception.UserNotFoundException;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException();
        }

        return userRepository.findByIdWithRole(userDetails.getUserId())
                .orElseThrow(UserNotFoundException::new);
    }
}