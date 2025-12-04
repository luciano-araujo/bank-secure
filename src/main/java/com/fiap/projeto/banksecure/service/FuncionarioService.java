package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Funcionario;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    public void validarCadastro(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário é obrigatório.");
        }

        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário é obrigatório.");
        }

        if (funcionario.getCpf() == null || funcionario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do funcionário é obrigatório.");
        }

        if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail do funcionário é obrigatório.");
        }

        if (funcionario.getSenha() == null || funcionario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do funcionário é obrigatória.");
        }

        if (funcionario.getTelefone() == null || funcionario.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do funcionário é obrigatório.");
        }
    }

    public boolean validarLogin(Funcionario funcionarioCadastrado, String senhaDigitada) {
        if (funcionarioCadastrado == null) {
            return false;
        }

        if (senhaDigitada == null || senhaDigitada.isBlank()) {
            return false;
        }

        return funcionarioCadastrado.getSenha().equals(senhaDigitada);
    }
}