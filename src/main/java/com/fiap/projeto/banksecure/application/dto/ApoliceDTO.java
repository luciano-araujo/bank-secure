package com.fiap.projeto.banksecure.application.dto;

import com.fiap.projeto.banksecure.domain.entity.Apolice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ApoliceDTO(
        UUID id,

        @NotNull(message = "Obrigatorio informar o ID do cliente")
        UUID clienteId,

        @NotNull(message = "Obrigatorio informar o valor total da cobertura")
        @DecimalMin(value = "0.01", inclusive = true, message = "Total de cobertura deve ser positivo")
        BigDecimal totalCobertura,

        @NotNull(message = "Obrigatorio informar o premio final")
        @DecimalMin(value = "0.01", inclusive = true, message = "Premio final deve ser positivo")
        BigDecimal premioFinal,

        @NotNull(message = "Obrigatorio informar a data de inicio da apolice")
        LocalDate dataInicial,

        @NotNull(message = "Obrigatorio informar a data de vencimento da apolice")
        LocalDate dataVencimento,

        @NotNull(message = "Obrigatorio informar o ID do seguro")
        UUID seguroId) {

    public static ApoliceDTO fromEntity(Apolice apolice) {
        return new ApoliceDTO(
                apolice.getId(),
                apolice.getCliente().getId(),
                apolice.getTotalCobertura(),
                apolice.getPremioFinal(),
                apolice.getDataInicial(),
                apolice.getDataVencimento(),
                apolice.getSeguro().getId()
        );
    }

    public Apolice toEntity() {
        Apolice apolice = new Apolice();
        apolice.setId(this.id());
        apolice.setTotalCobertura(this.totalCobertura());
        apolice.setDataInicial(this.dataInicial());
        apolice.setDataVencimento(this.dataVencimento());
        return apolice;
    }
}
