package com.fiap.projeto.banksecure.application.controller;

import com.fiap.projeto.banksecure.application.dto.AuthRequest;
import com.fiap.projeto.banksecure.application.dto.AuthResponse;
import com.fiap.projeto.banksecure.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }
}
