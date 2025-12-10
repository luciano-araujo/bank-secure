package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    public void validarCadastro(Cliente cliente) {
    private ClienteRepository clienteRepository;

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarCliente(cliente);
        validarIdade(cliente.getDataNascimento());
        return clienteRepository.save(cliente);
    }

    //[RF04] Cliente deve ter: Nome, CPF, Data de Nascimento.
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
        // Valida se data existe
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }

        LocalDate hoje = LocalDate.now();
        // 1. Calcula idade exata
        int idade = Period.between(dataNascimento, hoje).getYears();

        // 3. Valida idade mínima [RF05]
        if (idade < 18) {
            throw new IllegalArgumentException(
                    String.format("Cliente deve ser maior de 18 anos. Idade: %d anos", idade)
            );
        }
    }

    public Cliente cadastrarCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes(){
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes;
    }
}