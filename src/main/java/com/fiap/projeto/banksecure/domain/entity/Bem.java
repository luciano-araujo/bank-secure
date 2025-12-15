package com.fiap.projeto.banksecure.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "apolice_id", nullable = false)
    private Apolice apolice;
}
