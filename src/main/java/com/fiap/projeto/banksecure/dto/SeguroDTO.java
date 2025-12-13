package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record SeguroDTO(
        UUID id,
        String titulo,
        TipoSeguroEnum tipo,
        BigDecimal coberturaMinima,
        BigDecimal valorPremioBase
) {
    public Seguro toEntity() {
        Seguro seguro = new Seguro();
        seguro.setId(this.id);
        seguro.setTitulo(this.titulo);
        seguro.setTipo(this.tipo);
        seguro.setCoberturaMinima(this.coberturaMinima);
        seguro.setValorPremioBase(this.valorPremioBase);
        return seguro;
    }

    public static SeguroDTO fromEntity(Seguro seguro) {
        return new SeguroDTO(
                seguro.getId(),
                seguro.getTitulo(),
                seguro.getTipo(),
                seguro.getCoberturaMinima(),
                seguro.getValorPremioBase()
        );
    }
}
