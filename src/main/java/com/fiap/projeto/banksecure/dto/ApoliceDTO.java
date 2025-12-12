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

        @NotNull(message = "Obrigatório informar o ID do cliente")
        UUID clienteId,

        @NotNull(message = "Obrigatório informar o valor total da cobertura")
        BigDecimal totalCobertura,

        @NotNull(message = "Obrigatório informar a data de início da apólice")
        LocalDate dataInicial,

        @NotNull(message = "Obrigatório informar a data de vencimento da apólice")
        LocalDate dataVencimento,

        @NotNull(message = "A lista de bens é obrigatória")
        @Size(min = 1, message = "A apólice deve conter pelo menos um bem")
        List<BemDTO> listaDeBens,

        @NotNull(message = "Obrigatório informar o ID do seguro")
        UUID seguroId
) {

    public static ApoliceDTO fromEntity(Apolice apolice) {

        List<BemDTO> bens = apolice.getListaDeBens()
                .stream()
                .map(BemDTO::fromEntity)
                .toList();

        return new ApoliceDTO(
                apolice.getId(),
                apolice.getCliente().getId(),
                apolice.getTotalCobertura(),
                apolice.getDataInicial(),
                apolice.getDataVencimento(),
                bens,
                apolice.getSeguro().getId()
        );
    }

    // Cliente e Seguro devem ser setados no serviço, pois precisam ser buscados no banco!!!
    // A lista de bens deve ser convertida para entidades Bem no serviço
    public Apolice toEntity() {
        Apolice apolice = new Apolice();
        apolice.setId(this.id());
        apolice.setTotalCobertura(this.totalCobertura());
        apolice.setDataInicial(this.dataInicial());
        apolice.setDataVencimento(this.dataVencimento());
        return apolice;
    }
}
