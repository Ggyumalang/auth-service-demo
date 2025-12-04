package com.example.auth.service;

import com.example.auth.domain.user.User;
import com.example.auth.domain.user.UserRepository;
import com.example.auth.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
