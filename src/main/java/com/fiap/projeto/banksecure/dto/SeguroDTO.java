package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SeguroDTO(
        UUID id,

        @NotBlank(message = "O título do seguro é obrigatório")
        String titulo,

        @NotNull(message = "O valor do prêmio base é obrigatório")
        BigDecimal valorPremioBase,

        @NotNull(message = "O valor de cobertura mínima é obrigatório")
        String coberturaMinima,

        @NotBlank(message = "A descrição do seguro é obrigatória")
        String descricao,

        @NotNull(message = "O tipo do seguro é obrigatório")
        TipoSeguroEnum tipoSeguroEnum,

        @NotNull(message = "A lista de apólices é obrigatória")
        @Size(min = 1, message = "O seguro deve conter pelo menos uma apólice")
        List<ApoliceDTO> apolices
) {

    public static SeguroDTO fromEntity(Seguro seguro) {

        List<ApoliceDTO> apolicesDTO = seguro.getApolices()
                .stream()
                .map(ApoliceDTO::fromEntity)
                .toList();

        return new SeguroDTO(
                seguro.getId(),
                seguro.getTitulo(),
                seguro.getValorPremioBase(),
                seguro.getCoberturaMinima(),
                seguro.getDescricao(),
                seguro.getTipoSeguroEnum(),
                apolicesDTO
        );
    }

    // As apólices devem ser convertidas para entidades Apolice na service
    public Seguro toEntity() {
        Seguro seguro = new Seguro();
        seguro.setId(this.id());
        seguro.setTitulo(this.titulo());
        seguro.setValorPremioBase(this.valorPremioBase());
        seguro.setCoberturaMinima(this.coberturaMinima());
        seguro.setDescricao(this.descricao());
        seguro.setTipoSeguroEnum(this.tipoSeguroEnum());
        return seguro;
    }
}
