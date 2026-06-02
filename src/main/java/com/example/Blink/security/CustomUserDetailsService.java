package com.example.Blink.security;

import com.example.Blink.exception.UserNotActiveException;
import com.example.Blink.exception.UserNotFoundException;
import com.example.Blink.user.entity.User;
import com.example.Blink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmailWithRole(username)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        return new CustomUserDetails(user);
    }
}
