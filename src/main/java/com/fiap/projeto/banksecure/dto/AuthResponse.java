package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.enums.TipoUsuarioEnum;

import java.util.UUID;

public record AuthResponse(
        boolean authenticated,
        UUID usuarioId,
        String nome,
        TipoUsuarioEnum tipoUsuario
) {}
