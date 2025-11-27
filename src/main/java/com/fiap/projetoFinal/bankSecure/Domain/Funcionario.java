package com.fiap.projetoFinal.bankSecure.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Funcionario {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
}
