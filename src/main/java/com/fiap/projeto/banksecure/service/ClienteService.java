package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void validarCadastro(Cliente cliente) {
        validarCliente(cliente);
        validarIdade(cliente.getDataNascimento());
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarCadastro(cliente);
        return clienteRepository.save(cliente);
    }

    // [RF04] Cliente deve ter: Nome, CPF, Data de Nascimento.
    protected void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório.");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        if (cliente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }
    }

    // [RF05] Regra de Elegibilidade (Idade)
    private void validarIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }

        LocalDate hoje = LocalDate.now();
        int idade = Period.between(dataNascimento, hoje).getYears();

        if (idade < 18) {
            throw new IllegalArgumentException(
                    String.format("Cliente deve ser maior de 18 anos. Idade: %d anos", idade)
            );
        }
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
}
