package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Apolice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ApoliceDTO(
        UUID id,

        @NotNull(message = "Obrigatório informar o ID do cliente") UUID clienteId,

        @NotNull(message = "Obrigatório informar o valor total da cobertura") BigDecimal totalCobertura,

        BigDecimal premioFinal,

        @NotNull(message = "Obrigatório informar a data de início da apólice") LocalDate dataInicial,

        @NotNull(message = "Obrigatório informar a data de vencimento da apólice") LocalDate dataVencimento,

        @NotNull(message = "Obrigatório informar o ID do cliente") UUID seguroId) {

    public static ApoliceDTO fromEntity(Apolice apolice) {

        return new ApoliceDTO(
                apolice.getId(),
                apolice.getCliente().getId(),
                apolice.getTotalCobertura(),
                apolice.getPremioFinal(),
                apolice.getDataInicial(),
                apolice.getDataVencimento(),
                apolice.getSeguro().getId());
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
