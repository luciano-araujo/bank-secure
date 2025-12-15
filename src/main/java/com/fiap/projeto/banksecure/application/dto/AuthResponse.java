package com.fiap.projeto.banksecure.application.dto;

import com.fiap.projeto.banksecure.domain.enums.TipoUsuarioEnum;

import java.util.UUID;

public record AuthResponse(
        boolean authenticated,
        UUID usuarioId,
        String nome,
        TipoUsuarioEnum tipoUsuario
) {}
