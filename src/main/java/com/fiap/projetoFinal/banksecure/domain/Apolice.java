package com.fiap.projetoFinal.banksecure.domain;

import com.fiap.projetoFinal.banksecure.enums.TipoSeguroEnum;
import jakarta.persistence.*;
import lombok.*;

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
    @Id // Marca o campo como chave primária da entidade
    @GeneratedValue(strategy = GenerationType.UUID) // Gera o valor da chave primária automaticamente.
    private UUID id;

    @ManyToOne // Define um relacionamento muitos-para-um (esta apólice pertence a um cliente)
    @JoinColumn(name = "cliente_id", nullable = false) // Especifica a coluna de junção no banco (nome da foreign key)
    private Cliente cliente;

    private BigDecimal totalCobertura;
    private LocalDate vencimento;

    @Enumerated(EnumType.STRING) // Mapeia um enum para string no banco (armazenando o nome do enum)
    private TipoSeguroEnum tipoSeguroEnum;

    @OneToMany(mappedBy = "apolice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bem> listaDeBens = new ArrayList<>();
}
