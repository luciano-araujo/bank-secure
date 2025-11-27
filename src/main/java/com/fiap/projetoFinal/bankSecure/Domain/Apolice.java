package com.fiap.projetoFinal.bankSecure.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Apolice {
    private Long id;
    private Cliente cliente;
    private BigDecimal totalCobertura;
    private LocalDate vencimento;
    private TipoSeguroEnum tipoSeguroEnum;
    private List<Bem> listaDeBens;
}
