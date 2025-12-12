package com.fiap.projeto.banksecure.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record SeguroRequest(
        @NotBlank
        String titulo,
        @NotBlank
        String coberturaMinima,
        @NotBlank
        BigDecimal valorPremioBase
) {}
