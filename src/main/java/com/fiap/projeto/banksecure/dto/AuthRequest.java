package com.fiap.projeto.banksecure.dto;

public record AuthRequest(
        String email,
        String senha
) {}
