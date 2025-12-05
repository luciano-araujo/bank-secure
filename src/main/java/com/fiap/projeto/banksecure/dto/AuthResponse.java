package com.fiap.projeto.banksecure.dto;

import java.util.UUID;

public record AuthResponse(
        boolean authenticated,
        UUID FuncionarioId,
        String nome
) {}
