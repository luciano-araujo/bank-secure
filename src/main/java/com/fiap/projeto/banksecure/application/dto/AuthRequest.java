package com.fiap.projeto.banksecure.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank @Email String email,
        @NotBlank String senha
) {}
