package com.fiap.projeto.banksecure.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seguros")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "cobertura_minima")
    private String coberturaMinima;

    @Column(name = "valor_premio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPremioBase;

    public Seguro(String titulo, String coberturaMinima, BigDecimal valorPremioBase) {
        this.titulo = titulo;
        this.coberturaMinima = coberturaMinima;
        this.valorPremioBase = valorPremioBase;
    }
}
