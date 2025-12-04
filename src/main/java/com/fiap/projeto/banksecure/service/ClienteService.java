package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    public void validarCadastro(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório.");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }

        if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente é obrigatório.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail do cliente é obrigatório.");
        }

        if (cliente.getSenha() == null || cliente.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do cliente é obrigatória.");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do cliente é obrigatório.");
        }
    }
}