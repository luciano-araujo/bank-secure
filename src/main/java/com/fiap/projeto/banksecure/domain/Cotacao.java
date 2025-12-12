package com.fiap.projeto.banksecure.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cotacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "seguro_id", nullable = false)
    private Seguro seguro;

    @Column(nullable = false)
    private BigDecimal premioBase;

    @Column(nullable = false)
    private BigDecimal premioFinal;

    @Column(nullable = false)
    private LocalDate dataCalculo;
}
