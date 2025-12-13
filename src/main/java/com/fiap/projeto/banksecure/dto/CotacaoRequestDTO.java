package com.fiap.projeto.banksecure.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CotacaoRequestDTO(
        @NotNull UUID clienteId,
        @NotNull UUID seguroId,
        @NotNull BigDecimal coberturaTotal
) {
}
