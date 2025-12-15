package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Seguro;

import java.math.BigDecimal;
import java.util.UUID;

public record SeguroDTO(
        UUID id,
        @jakarta.validation.constraints.NotBlank(message = "T\u00edtulo \u00e9 obrigat\u00f3rio")
        String titulo,

        @jakarta.validation.constraints.NotBlank(message = "Cobertura m\u00ednima \u00e9 obrigat\u00f3ria")
        String coberturaMinima,

        @jakarta.validation.constraints.NotNull(message = "Valor do pr\u00eamio base \u00e9 obrigat\u00f3rio")
        @jakarta.validation.constraints.DecimalMin(value = "0.01", inclusive = true, message = "Valor do pr\u00eamio base deve ser positivo")
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
