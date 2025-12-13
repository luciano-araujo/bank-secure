package com.fiap.projeto.banksecure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BemCadastroDTO(
        @NotBlank(message = "O título do bem é obrigatório")
        String titulo,

        @NotNull(message = "O valor do bem é obrigatório")
        BigDecimal valor
) {
}
