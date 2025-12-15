package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FuncionarioService {

    private final FuncionarioRepository funciRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (funcionario.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }

        if (funcionario.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        }

        int idade = Period.between(funcionario.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 18) {
            throw new IllegalArgumentException("O funcionário deve ter pelo menos 18 anos.");
        }
    }

    public FuncionarioDTO cadastrarFuncionario(FuncionarioDTO funciDTO) throws IllegalArgumentException {
        Funcionario funcionario = funciDTO.toEntitySemSenha();

        // Criptografia da senha
        String senhaCriptografada = passwordEncoder.encode(funciDTO.senha());
        funcionario.setSenha(senhaCriptografada);

        validarCadastro(funcionario);
        Funcionario funciCadastrado = funciRepository.save(funcionario);

        return FuncionarioDTO.fromEntity(funciCadastrado);
    }

    public FuncionarioDTO atualizarFuncionario(UUID id, FuncionarioDTO funciDTO) throws IllegalArgumentException {
        Funcionario funcionarioExistente = funciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário nao encontrado id: " + id));

        funcionarioExistente.setNome(funciDTO.nome());
        funcionarioExistente.setCpf(funciDTO.cpf());
        funcionarioExistente.setEmail(funciDTO.email());
        funcionarioExistente.setTelefone(funciDTO.telefone());
        funcionarioExistente.setDataNascimento(funciDTO.dataNascimento());

        if (funciDTO.senha() != null && !funciDTO.senha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(funciDTO.senha());
            funcionarioExistente.setSenha(senhaCriptografada);
        }

        validarCadastro(funcionarioExistente);
        Funcionario funciAtualizado = funciRepository.save(funcionarioExistente);

        return FuncionarioDTO.fromEntity(funciAtualizado);
    }

    public void deletarFuncionario(UUID id) throws RuntimeException {
        Funcionario funcionario = funciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        funciRepository.delete(funcionario);
    }

    public FuncionarioDTO buscarPorId(UUID id) throws RuntimeException {
        Funcionario funcionario = funciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        return FuncionarioDTO.fromEntity(funcionario);
    }

    public List<FuncionarioDTO> getAllFuncionarios() {
        return funciRepository.findAll().stream()
                .map(FuncionarioDTO::fromEntity)
                .toList();
    }
}