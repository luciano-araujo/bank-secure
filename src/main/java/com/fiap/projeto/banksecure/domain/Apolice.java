package com.fiap.projeto.banksecure.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "apolices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apolice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "seguro_id", nullable = false)
    private Seguro seguro;

    @Column(name = "premio_final", nullable = false)
    private BigDecimal premioFinal;

    @Column(nullable = false)
    private LocalDate dataInicial;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Column(nullable = false)
    private BigDecimal totalCobertura;

    @OneToMany(mappedBy = "apolice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bem> listaDeBens = new ArrayList<>();
}
