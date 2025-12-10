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

    public Cliente buscarPorId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }

        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public void validarCadastro(Cliente cliente) {
        validarClienteSimples(cliente);
        validarIdade(cliente.getDataNascimento());
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarCadastro(cliente);
        validarIdade(cliente.getDataNascimento());
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente atualizar(UUID id, Cliente clienteAtualizado) {
        // Busca cliente existente
        Cliente clienteExistente = buscarPorId(id);

        // Valida dados do cliente atualizado
        validarCliente(clienteAtualizado);

        // Valida idade se foi alterada
        if (!clienteExistente.getDataNascimento().equals(clienteAtualizado.getDataNascimento())) {
            validarIdade(clienteAtualizado.getDataNascimento());
        }

        // Valida CPF se foi alterado
        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf())) {
            if (clienteRepository.existsByCpf(clienteAtualizado.getCpf())) {
                throw new IllegalArgumentException("CPF já cadastrado para outro cliente");
            }
        }

        // Atualiza os campos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void excluir(UUID id) {
        // Verifica se cliente existe
        Cliente cliente = buscarPorId(id);

        // TODO: Verificar se cliente tem apólices ativas antes de excluir
        // if (apoliceRepository.existsByClienteId(id)) {
        //     throw new IllegalStateException("Não é possível excluir cliente com apólices ativas");
        // }

        clienteRepository.delete(cliente);
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

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        if (cliente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }
    }

    protected void validarClienteSimples(Cliente cliente) {
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
