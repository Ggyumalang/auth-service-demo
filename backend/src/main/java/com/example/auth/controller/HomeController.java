package com.example.auth.controller;

import com.example.auth.dto.UserResponse;
import com.example.auth.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Home", description = "Home API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "User Home")
    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUserInfo(Principal principal) {
        return ResponseEntity.ok(homeService.getUserInfo(principal.getName()));
    }
}
