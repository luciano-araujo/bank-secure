package com.fiap.projeto.banksecure.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record NovaApoliceDTO(
        @NotNull UUID clienteId,
        @NotNull UUID seguroId,
        @NotNull LocalDate dataInicial,
        @NotNull LocalDate dataVencimento,
        @NotEmpty List<BemCadastroDTO> bens
) {
}
