package com.fiap.projeto.banksecure.dto;

import com.fiap.projeto.banksecure.domain.Funcionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FuncionarioDTO(
        UUID id,
        @NotBlank(message = "Nome do usuário é obrigatório")
        @Size(min = 3, max = 30, message = "Nome deve ter entre 3 e 30 caracteres")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 caracteres")
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String senha,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Telefone deve seguir E.164, ex: +5511987654321")
        @NotBlank(message = "Telefone é obrigatório")
        String telefone
) {

    public static FuncionarioDTO fromEntity(Funcionario funcionario){
       return  new FuncionarioDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getEmail(),
                null,
                funcionario.getTelefone()
       );
    }

    public Funcionario toEntity(){
        Funcionario funcionario = new Funcionario();
        funcionario.setId(this.id());
        funcionario.setNome(this.nome());
        funcionario.setCpf(this.cpf());
        funcionario.setEmail(this.email());
        funcionario.setSenha(this.senha());
        funcionario.setTelefone(this.telefone());
        return funcionario;
    }
}
