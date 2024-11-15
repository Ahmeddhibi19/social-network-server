package com.Ahmed.service;

import com.Ahmed.common.UserPrincipal;
import com.Ahmed.entity.User;
import com.Ahmed.exception.UserNotFoundException;
import com.Ahmed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user exists with this email.");
        } else {
            return new UserPrincipal(user.get());
        }
    }
}
