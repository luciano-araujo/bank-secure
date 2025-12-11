package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.dto.ClienteDTO;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteDTO buscarPorId(UUID id) throws RuntimeException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return ClienteDTO.fromEntity(cliente);
    }

    public Cliente buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findAll().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst().orElse(null);

        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        return cliente;
    }

    public void validarCadastro(Cliente cliente) {
        validarClienteSimples(cliente);
        validarIdade(cliente.getDataNascimento());
    }


    public ClienteDTO cadastrarCliente(ClienteDTO clienteDTO) throws IllegalArgumentException {
        Cliente cliente = clienteDTO.toEntitySemSenha();

        String senhaCriptografada = passwordEncoder.encode(clienteDTO.senha());
        cliente.setSenha(senhaCriptografada);

        validarCadastro(cliente);
        Cliente clienteCadastrado = clienteRepository.save(cliente);

        return ClienteDTO.fromEntity(clienteCadastrado);
    }

    //atualizar ok
    public ClienteDTO atualizarCliente(UUID id, ClienteDTO clienteDTO) throws IllegalArgumentException {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado id: " + id));

        clienteExistente.setNome(clienteDTO.nome());
        clienteExistente.setCpf(clienteDTO.cpf());
        clienteExistente.setEmail(clienteDTO.email());
        clienteExistente.setTelefone(clienteDTO.telefone());
        clienteExistente.setDataNascimento(clienteDTO.dataNascimento());

        if (clienteDTO.senha() != null && !clienteDTO.senha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(clienteDTO.senha());
            clienteExistente.setSenha(senhaCriptografada);
        }

        validarCadastro(clienteExistente);
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);

        return ClienteDTO.fromEntity(clienteAtualizado);
    }

/*    @Transactional
    public void excluir(UUID id) {
        // Verifica se cliente existe
        Cliente cliente = buscarPorId(id);

        // TODO: Verificar se cliente tem apólices ativas antes de excluir
        // if (apoliceRepository.existsByClienteId(id)) {
        //     throw new IllegalStateException("Não é possível excluir cliente com apólices ativas");
        // }

        clienteRepository.delete(cliente);
    }*/

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

        if (cliente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        }

        int idade = Period.between(cliente.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 18) {
            throw new IllegalArgumentException("O cliente deve ter pelo menos 18 anos.");
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

    public List<ClienteDTO> getAllClientes(){
        return clienteRepository.findAll().stream()
                .map(ClienteDTO::fromEntity)
                .toList();
    }
}
