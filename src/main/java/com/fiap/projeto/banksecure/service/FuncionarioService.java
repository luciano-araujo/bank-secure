package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FuncionarioService {
    @Autowired
    private FuncionarioRepository repository;

    // Poderia ser adicionada para melhor criptografia
    // @Autowired
    // private PasswordEncoder passwordEncoder;

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

    public Funcionario cadastrarFuncionario(FuncionarioDTO dto) throws IllegalArgumentException {
        Funcionario funcionario = dto.toEntity();
        validarCadastro(funcionario);

        return repository.save(funcionario);
    }

    public Funcionario atualizarFuncionario(FuncionarioDTO dto) throws IllegalArgumentException {
        Funcionario funcionario = dto.toEntity();
        validarCadastro(funcionario);

        return repository.save(funcionario);
    }

    public void excluirFuncionario(UUID id) throws RuntimeException {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        repository.delete(funcionario);
    }

    public Funcionario buscarPorId(UUID id) throws RuntimeException {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public boolean logar(AuthRequest request){
        Funcionario funcionario = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // boolean senhaCorreta = passwordEncoder.matches(request.senha(), funcionario.getSenha());
        boolean senhaCorreta = funcionario.getSenha().equals(request.senha());

        if (!senhaCorreta) {
            throw new RuntimeException("Senha inválida");
        }

        return new AuthResponse(
                true,
                funcionario.getId(),
                funcionario.getNome()
        ).authenticated();
    }
}