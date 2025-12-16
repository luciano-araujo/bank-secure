package com.fiap.projeto.banksecure.application.dto;

import com.fiap.projeto.banksecure.domain.entity.Funcionario;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioDTO(
        UUID id,
        @NotBlank(message = "Nome do usuário é obrigatório")
        @Size(min = 3, max = 30, message = "Nome deve ter entre 3 e 30 caracteres")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String senha,

        @Pattern(regexp = "^\\(\\d{2}\\) \\d{5}-\\d{4}$", message = "Telefone deve seguir o padrão (11) 98765-4321")
        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento
) {

    public static FuncionarioDTO fromEntity(Funcionario funcionario){
       return  new FuncionarioDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getEmail(),
                funcionario.getSenha(),
                funcionario.getTelefone(),
                funcionario.getDataNascimento()
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
        funcionario.setDataNascimento(this.dataNascimento());
        return funcionario;
    }

    public Funcionario toEntitySemSenha() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(this.id());
        funcionario.setNome(this.nome());
        funcionario.setCpf(this.cpf());
        funcionario.setEmail(this.email());
        funcionario.setTelefone(this.telefone());
        funcionario.setDataNascimento(this.dataNascimento());
        return funcionario;
    }

}
