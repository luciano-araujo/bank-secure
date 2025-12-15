package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.dto.ClienteDTO;
import com.fiap.projeto.banksecure.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ApoliceRepository apoliceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Cliente cliente = criarClienteCompleto(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = clienteService.buscarPorId(id);

        assertNotNull(resultado);
        verify(clienteRepository).findById(id);
    }

    @Test
    @DisplayName("Deve falhar ao buscar ID inexistente")
    void deveFalharBuscarIdInexistente() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteService.buscarPorId(id));
        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarPorCpf() {
        String cpfAlvo = "12345678900";
        Cliente c1 = criarClienteCompleto(UUID.randomUUID());
        c1.setCpf("00000000000");

        Cliente c2 = criarClienteCompleto(UUID.randomUUID());
        c2.setCpf(cpfAlvo);

        when(clienteRepository.findAll()).thenReturn(List.of(c1, c2));

        Cliente resultado = clienteService.buscarPorCpf(cpfAlvo);

        assertNotNull(resultado);
        assertEquals(cpfAlvo, resultado.getCpf());
    }

    @Test
    @DisplayName("Deve falhar ao buscar CPF inexistente")
    void deveFalharBuscarCpfInexistente() {
        String cpfBusca = "99999999999";
        when(clienteRepository.findAll()).thenReturn(List.of(criarClienteCompleto(UUID.randomUUID())));

        assertThrows(IllegalArgumentException.class, () -> clienteService.buscarPorCpf(cpfBusca));
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso (Criptografando senha)")
    void deveCadastrarCliente() {

        String senhaPura = "senha123";
        String senhaHash = "hashSeguro";

        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.senha()).thenReturn(senhaPura);

        Cliente clienteMapeado = criarClienteCompleto(null);
        clienteMapeado.setSenha(null);

        when(dtoMock.toEntitySemSenha()).thenReturn(clienteMapeado);

        when(passwordEncoder.encode(senhaPura)).thenReturn(senhaHash);

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ClienteDTO resultado = clienteService.cadastrarCliente(dtoMock);

        assertNotNull(resultado);

        assertEquals(senhaHash, clienteMapeado.getSenha());
        verify(passwordEncoder).encode(senhaPura);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve falhar cadastro se cliente for menor de idade")
    void deveFalharCadastroMenorIdade() {

        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.senha()).thenReturn("123");

        Cliente clienteMenor = criarClienteCompleto(null);
        clienteMenor.setDataNascimento(LocalDate.now().minusYears(15)); // 15 Anos

        when(dtoMock.toEntitySemSenha()).thenReturn(clienteMenor);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clienteService.cadastrarCliente(dtoMock));

        assertTrue(ex.getMessage().contains("18 anos"));
        verify(clienteRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve atualizar cliente existente e trocar senha")
    void deveAtualizarCliente() {
        // Arrange
        UUID id = UUID.randomUUID();
        Cliente clienteExistente = criarClienteCompleto(id);

        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.nome()).thenReturn("Novo Nome");
        when(dtoMock.cpf()).thenReturn("11122233344");
        when(dtoMock.email()).thenReturn("novo@email.com");
        when(dtoMock.telefone()).thenReturn("99998888");
        when(dtoMock.dataNascimento()).thenReturn(LocalDate.of(1995, 1, 1));
        when(dtoMock.senha()).thenReturn("novaSenha"); // Quer trocar a senha

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaHash");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        ClienteDTO resultado = clienteService.atualizarCliente(id, dtoMock);

        assertEquals("Novo Nome", clienteExistente.getNome());
        assertEquals("novaHash", clienteExistente.getSenha()); // Senha deve ser o hash
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    @DisplayName("Deve falhar atualização se ID não existe")
    void deveFalharAtualizacaoIdInexistente() {
        UUID id = UUID.randomUUID();
        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clienteService.atualizarCliente(id, dtoMock));
    }

    @Test
    @DisplayName("Deve deletar cliente sem apólices")
    void deveDeletarClienteSemApolices() {
        UUID id = UUID.randomUUID();
        Cliente cliente = criarClienteCompleto(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(apoliceRepository.existsByClienteId(id)).thenReturn(false); // Não tem apólice

        clienteService.deletarCliente(id);

        verify(clienteRepository).delete(cliente);
    }

    @Test
    @DisplayName("Deve impedir deleção se tiver apólices")
    void deveFalharDelecaoComApolices() {
        UUID id = UUID.randomUUID();
        Cliente cliente = criarClienteCompleto(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(apoliceRepository.existsByClienteId(id)).thenReturn(true); // Tem apólice

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clienteService.deletarCliente(id));

        assertEquals("Não é possível excluir cliente com apólices ativas", ex.getMessage());
        verify(clienteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve listar todos")
    void deveListarTodos() {
        when(clienteRepository.findAll()).thenReturn(List.of(criarClienteCompleto(UUID.randomUUID())));

        List<ClienteDTO> lista = clienteService.getAllClientes();
        assertFalse(lista.isEmpty());
    }

    private Cliente criarClienteCompleto(UUID id) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNome("Usuario Teste");
        c.setCpf("12345678900");
        c.setEmail("teste@email.com");
        c.setTelefone("11999999999");
        c.setDataNascimento(LocalDate.of(1990, 1, 1)); // 30+ anos (Maior de idade)
        c.setSenha("hashSenhaAntiga");
        return c;
    }

    // =========================================================================
    // NOVOS TESTES PARA AUMENTAR COBERTURA (> 80%)
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar cliente mantendo a senha antiga (Branch: senha nula/vazia)")
    void deveAtualizarClienteSemTrocarSenha() {
        UUID id = UUID.randomUUID();
        Cliente clienteExistente = criarClienteCompleto(id);
        String senhaAntiga = clienteExistente.getSenha();

        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.nome()).thenReturn("Nome Atualizado");
        when(dtoMock.cpf()).thenReturn("12345678900");
        when(dtoMock.email()).thenReturn("email@teste.com");
        when(dtoMock.telefone()).thenReturn("1199999999");
        when(dtoMock.dataNascimento()).thenReturn(LocalDate.of(1990, 1, 1));

        // CENÁRIO: Senha vem vazia ou nula no DTO
        when(dtoMock.senha()).thenReturn("");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        clienteService.atualizarCliente(id, dtoMock);

        // Assert
        assertEquals(senhaAntiga, clienteExistente.getSenha()); // A senha deve permanecer a mesma
        verify(passwordEncoder, never()).encode(anyString()); // O encoder NÃO deve ser chamado
    }

    @Test
    @DisplayName("Deve falhar validação simples se CPF for nulo ou vazio")
    void deveFalharValidacaoCpf() {
        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.senha()).thenReturn("123");

        Cliente clienteSemCpf = new Cliente();
        clienteSemCpf.setNome("Teste");
        clienteSemCpf.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteSemCpf.setCpf(null); // Caso nulo

        when(dtoMock.toEntitySemSenha()).thenReturn(clienteSemCpf);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        // Teste Null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> clienteService.cadastrarCliente(dtoMock));
        assertEquals("CPF é obrigatório.", ex1.getMessage());

        // Teste Vazio
        clienteSemCpf.setCpf("");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> clienteService.cadastrarCliente(dtoMock));
        assertEquals("CPF é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação simples se Data de Nascimento for nula")
    void deveFalharValidacaoDataNascimento() {
        ClienteDTO dtoMock = mock(ClienteDTO.class);
        when(dtoMock.senha()).thenReturn("123");

        Cliente clienteSemData = new Cliente();
        clienteSemData.setNome("Teste");
        clienteSemData.setCpf("123");
        clienteSemData.setDataNascimento(null); // Data Nula

        when(dtoMock.toEntitySemSenha()).thenReturn(clienteSemData);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clienteService.cadastrarCliente(dtoMock));

        // A mensagem pode vir do validarClienteSimples ou validarIdade, dependendo da ordem
        assertTrue(ex.getMessage().contains("Data de nascimento é obrigatória"));
    }

    @Test
    @DisplayName("Deve falhar se o objeto Cliente for nulo")
    void deveFalharClienteNulo() {
        // Como validarCadastro é publico/protected, podemos testar a validação isolada
        assertThrows(IllegalArgumentException.class,
                () -> clienteService.validarCadastro(null));
    }

    // --- COBERTURA DO MÉTODO PROTECTED "validarCliente" ---
    // Este método existe na sua classe mas não é chamado pelo fluxo principal (cadastrarCliente).
    // Se ele existe, o SonarQube/JaCoCo cobra cobertura. Vamos testá-lo diretamente.

    @Test
    @DisplayName("Deve testar validações completas do método protegido validarCliente")
    void deveTestarMetodoProtegidoValidarCliente() {
        // 1. Teste Data Futura
        Cliente clienteFuturo = criarClienteCompleto(UUID.randomUUID());
        clienteFuturo.setDataNascimento(LocalDate.now().plusDays(1));

        IllegalArgumentException exFuturo = assertThrows(IllegalArgumentException.class,
                () -> clienteService.validarCliente(clienteFuturo));
        assertEquals("Data de nascimento não pode ser futura.", exFuturo.getMessage());

        // 2. Teste CPF Duplicado (Regra exclusiva desse método)
        Cliente clienteDuplicado = criarClienteCompleto(UUID.randomUUID());
        when(clienteRepository.existsByCpf(clienteDuplicado.getCpf())).thenReturn(true);

        IllegalArgumentException exDuplicado = assertThrows(IllegalArgumentException.class,
                () -> clienteService.validarCliente(clienteDuplicado));
        assertEquals("CPF já cadastrado no sistema", exDuplicado.getMessage());

        // 3. Teste Nome Vazio
        Cliente clienteSemNome = criarClienteCompleto(UUID.randomUUID());
        clienteSemNome.setNome("");
        assertThrows(IllegalArgumentException.class, () -> clienteService.validarCliente(clienteSemNome));
    }
}