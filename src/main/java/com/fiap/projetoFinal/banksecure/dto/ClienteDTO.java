package com.fiap.projetoFinal.banksecure.dto;

import com.fiap.projetoFinal.banksecure.domain.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public record ClienteDTO(
        UUID id,
        // Validação: o nome não pode ser nulo ou vazio
        // Deve ter entre 3 e 30 caracteres
        @NotBlank(message = "Nome do usuário é obrigatório")
        @Size(min = 3, max = 30, message = "Nome deve ter entre 3 e 30 caracteres")
        String nome,

        // Validação: o CPF não pode ser nulo ou vazio
        // Deve ter exatamente 11 caracteres
        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 caracteres")
        String cpf,

        // Validação: o e-mail é obrigatório e deve seguir o formato válido
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        // Validação: a senha não pode ser vazia e deve ter entre 6 e 100 caracteres
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String senha,

        // Validação: o telefone não pode ser nulo ou vazio
        // Deve ter entre 10 e 15 caracteres
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Telefone deve seguir E.164, ex: +5511987654321")
        @NotBlank(message = "Telefone é obrigatório")
        String telefone
) {

    public static ClienteDTO fromEntity(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                null,
                cliente.getTelefone()
        );
    }

    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setId(this.id());
        cliente.setNome(this.nome());
        cliente.setCpf(this.cpf());
        cliente.setEmail(this.email());
        cliente.setSenha(this.senha());
        cliente.setTelefone(this.telefone());
        return cliente;
    }
}
