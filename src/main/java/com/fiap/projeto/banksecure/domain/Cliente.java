package com.fiap.projeto.banksecure.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false) // define regras para a coluna cpf que nao existam cpfs duplicados e nem nulos
    private String cpf;

    @Column(unique = true, nullable = false) // define regras para a coluna email que nao existam emails duplicados e nem nulos
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(length = 16 , nullable = false)
    private String telefone;
}
