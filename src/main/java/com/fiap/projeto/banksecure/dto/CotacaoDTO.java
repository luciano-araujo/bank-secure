package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CotacaoDTO(
        UUID id,
        UUID clienteId,
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
