package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.enums.TipoUsuarioEnum;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AuthResponse login(AuthRequest request) {
        // login cliente
        var clienteOpt = clienteRepository.findByEmail(request.email());
        if (clienteOpt.isPresent()) {
            var cliente = clienteOpt.get();
            if (!request.senha().equals(cliente.getSenha())) {
                throw new RuntimeException("Senha inválida");
            }
            return new AuthResponse(true, cliente.getId(), cliente.getNome(), TipoUsuarioEnum.CLIENTE);
        }

        // login funcionario
        var funcionarioOpt = funcionarioRepository.findByEmail(request.email());
        if (funcionarioOpt.isPresent()) {
            var funcionario = funcionarioOpt.get();
            if (!request.senha().equals(funcionario.getSenha())) {
                throw new RuntimeException("Senha inválida");
            }
            return new AuthResponse(true, funcionario.getId(), funcionario.getNome(), TipoUsuarioEnum.FUNCIONARIO);
        }
        throw new RuntimeException("Usuário não encontrado");
    }
}
