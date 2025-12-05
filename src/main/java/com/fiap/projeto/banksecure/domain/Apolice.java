package com.fiap.projeto.banksecure.domain;

import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "apolices") // Define o nome da tabela no banco
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apolice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne // Define um relacionamento muitos-para-um (esta apólice pertence a um cliente)
    @JoinColumn(name = "cliente_id", nullable = false) // Especifica a coluna de junção no banco (nome da foreign key)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSeguroEnum tipoSeguroEnum; // adicionado para refletir o tipo do seguro na apólice

    @Column(nullable = false)
    private BigDecimal totalCobertura;

    @Column(nullable = false)
    private LocalDate dataInicial;

    @Column(nullable = false)
    private LocalDate dataVencimento; // renomeado de dataVencimento para vencer/compatibilidade com serviço/console

    @OneToMany(mappedBy = "apolice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bem> listaDeBens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seguro_id", nullable = false)
    private Seguro seguro;
}
