package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Bem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BemDTO(
        UUID id,

        @NotBlank(message = "O título do bem é obrigatório")
        String titulo,

        @NotNull(message = "O valor do bem é obrigatório")
        BigDecimal valor,

        @NotNull(message = "O ID da apólice é obrigatório")
        UUID apoliceId
) {
    public static BemDTO fromEntity(Bem bem) {
        return new BemDTO(
                bem.getId(),
                bem.getTitulo(),
                bem.getValor(),
                bem.getApolice() != null ? bem.getApolice().getId() : null
        );
    }

    public Bem toEntity() {
        Bem bem = new Bem();
        bem.setId(this.id());
        bem.setTitulo(this.titulo());
        bem.setValor(this.valor());
        return bem;
    }
}
