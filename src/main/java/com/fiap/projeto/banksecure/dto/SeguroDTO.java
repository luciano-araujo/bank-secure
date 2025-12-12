package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Seguro;

import java.math.BigDecimal;
import java.util.UUID;

public record SeguroDTO(
        UUID id,
        String titulo,
        BigDecimal coberturaMinima,
        BigDecimal valorPremioBase
) {
    public Seguro toEntity() {
        Seguro seguro = new Seguro();
        seguro.setTitulo(this.titulo);
        seguro.setCoberturaMinima(this.coberturaMinima);
        seguro.setValorPremioBase(this.valorPremioBase);
        return seguro;
    }

    public static SeguroDTO fromEntity(Seguro seguro) {
        return new SeguroDTO(
                seguro.getId(),
                seguro.getTitulo(),
                seguro.getCoberturaMinima(),
                seguro.getValorPremioBase()
        );
    }
}
