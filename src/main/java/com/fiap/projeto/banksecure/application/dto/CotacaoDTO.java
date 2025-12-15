package com.fiap.projeto.banksecure.application.dto;

import com.fiap.projeto.banksecure.domain.entity.Cotacao;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CotacaoDTO(
        UUID id,
        @NotNull(message = "Cliente eh obrigatorio")
        UUID clienteId,
        @NotNull(message = "Seguro eh obrigatorio")
        UUID seguroId,
        BigDecimal premioBase,
        BigDecimal premioFinal,
        LocalDate dataCalculo
) {
    public static CotacaoDTO fromEntity(Cotacao cotacao) {
        return new CotacaoDTO(
                cotacao.getId(),
                cotacao.getCliente().getId(),
                cotacao.getSeguro().getId(),
                cotacao.getPremioBase(),
                cotacao.getPremioFinal(),
                cotacao.getDataCalculo()
        );
    }
}
