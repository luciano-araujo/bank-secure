package com.fiap.projetoFinal.banksecure.service;

import com.fiap.projetoFinal.banksecure.domain.Bem;
import com.fiap.projetoFinal.banksecure.enums.TipoSeguroEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class CotacaoService {

    private static final BigDecimal TAXA_PADRAO = new BigDecimal("0.05");
    private static final BigDecimal FATOR_RISCO = new BigDecimal("1.10");
    private static final BigDecimal BONUS_VIDA = new BigDecimal("100.00");

    public BigDecimal calcularTotalCobertura(List<Bem> bens) {
        if (bens == null || bens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Bem bem : bens) {
            if (bem != null && bem.getValor() != null) {
                total = total.add(bem.getValor());
            }
        }

        return total;
    }

    public BigDecimal calcularPremio(BigDecimal totalCobertura, TipoSeguroEnum tipo) {
        if (totalCobertura == null || totalCobertura.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total de cobertura deve ser maior que zero.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de seguro é obrigatório.");
        }

        BigDecimal taxaTipo = taxaPorTipo(tipo);

        BigDecimal base = totalCobertura.multiply(taxaTipo);

        BigDecimal valor = base.add(base.multiply(TAXA_PADRAO));

        if (tipo == TipoSeguroEnum.VIDA) {
            valor = valor.add(BONUS_VIDA);
        }

        valor = valor.multiply(FATOR_RISCO);

        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal taxaPorTipo(TipoSeguroEnum tipo) {
        return switch (tipo) {
            case RESIDENCIAL -> new BigDecimal("0.02");
            case AUTOMOTIVO -> new BigDecimal("0.03");
            case VIDA -> new BigDecimal("0.01");
        };
    }

    private int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória para cálculo de idade.");
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}