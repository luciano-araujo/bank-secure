package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Bem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BemDTO(
        UUID id,

        @NotBlank(message = "A descrição do bem é obrigatória")
        String descricao,

        @NotNull(message = "O valor do bem é obrigatório")
        BigDecimal valor,

        @NotNull(message = "O ID da apólice é obrigatório")
        UUID apoliceId
) {
    public static BemDTO fromEntity(Bem bem) {
        return new BemDTO(
                bem.getId(),
                bem.getDescricao(),
                bem.getValor(),
                bem.getApolice() != null ? bem.getApolice().getId() : null
        );
    }

    // apólice será setada na service
    public Bem toEntity() {
        Bem bem = new Bem();
        bem.setId(this.id());
        bem.setDescricao(this.descricao());
        bem.setValor(this.valor());
        return bem;
    }
}
