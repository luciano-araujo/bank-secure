package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Seguro;
import java.math.BigDecimal;
import java.util.UUID;

public record SeguroResponse(
        UUID id,
        String titulo,
        String coberturaMinima,
        BigDecimal valorPremioBase
) {
    public static SeguroResponse fromEntity(Seguro seguro) {
        return new SeguroResponse(
                seguro.getId(),
                seguro.getTitulo(),
                seguro.getCoberturaMinima(),
                seguro.getValorPremioBase()
        );
    }
}