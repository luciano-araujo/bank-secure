package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ApoliceDTO(
        UUID id,
        UUID clienteId,
        UUID seguroId,
        TipoSeguroEnum tipoSeguro,
        BigDecimal totalCobertura,
        BigDecimal premioFinal,
        LocalDate dataInicial,
        LocalDate dataVencimento,
        List<BemDTO> bens
) {
    public static ApoliceDTO fromEntity(Apolice apolice) {
        return new ApoliceDTO(
                apolice.getId(),
                apolice.getCliente().getId(),
                apolice.getSeguro().getId(),
                apolice.getTipoSeguro(),
                apolice.getTotalCobertura(),
                apolice.getPremioFinal(),
                apolice.getDataInicial(),
                apolice.getDataVencimento(),
                apolice.getListaDeBens()
                        .stream()
                        .map(BemDTO::fromEntity)
                        .toList()
        );
    }
}
