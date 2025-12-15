package com.fiap.projeto.banksecure.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String senha;

    @Column(length = 16 , nullable = false)
    private String telefone;
}
