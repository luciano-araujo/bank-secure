package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Funcionario;
import com.fiap.projeto.banksecure.application.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.infra.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.application.service.FuncionarioService;
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
class FuncionarioServiceTest {

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Mock
    private FuncionarioRepository funciRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve validar campos obrigatórios (Nulos ou Vazios)")
    void deveValidarCamposObrigatorios() {

        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(null));

        Funcionario f = criarFuncionarioValido();

        f.setNome(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setNome("");
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setNome("Carlos");

        f.setCpf(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setCpf("123");

        f.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setEmail("email@teste.com");

        f.setSenha(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setSenha("123");

        f.setTelefone(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
        f.setTelefone("999");

        f.setDataNascimento(null);
        assertThrows(IllegalArgumentException.class, () -> funcionarioService.validarCadastro(f));
    }

    @Test
    @DisplayName("Deve validar regras de Data de Nascimento (Futura e Menor de Idade)")
    void deveValidarRegrasDeData() {
        Funcionario f = criarFuncionarioValido();


        f.setDataNascimento(LocalDate.now().plusDays(1));
        IllegalArgumentException exFutura = assertThrows(IllegalArgumentException.class,
                () -> funcionarioService.validarCadastro(f));
        assertEquals("Data de nascimento não pode ser futura.", exFutura.getMessage());

        f.setDataNascimento(LocalDate.now().minusYears(17));
        IllegalArgumentException exIdade = assertThrows(IllegalArgumentException.class,
                () -> funcionarioService.validarCadastro(f));
        assertEquals("O funcionário deve ter pelo menos 18 anos.", exIdade.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar funcionário com sucesso (Senha criptografada)")
    void deveCadastrarFuncionario() {

        String senhaPura = "123456";
        String senhaHash = "hash123";

        FuncionarioDTO dtoMock = mock(FuncionarioDTO.class);
        when(dtoMock.senha()).thenReturn(senhaPura);

        Funcionario funcValido = criarFuncionarioValido();
        funcValido.setSenha(null);

        when(dtoMock.toEntitySemSenha()).thenReturn(funcValido);
        when(passwordEncoder.encode(senhaPura)).thenReturn(senhaHash);

        when(funciRepository.save(any(Funcionario.class))).thenAnswer(i -> {
            Funcionario f = i.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        FuncionarioDTO resultado = funcionarioService.cadastrarFuncionario(dtoMock);

        assertNotNull(resultado);
        assertEquals(senhaHash, funcValido.getSenha()); // Verifica se criptografou
        verify(funciRepository).save(funcValido);
    }

    @Test
    @DisplayName("Deve atualizar funcionário COM troca de senha")
    void deveAtualizarComSenha() {
        UUID id = UUID.randomUUID();
        Funcionario funcExistente = criarFuncionarioValido();
        funcExistente.setId(id);

        FuncionarioDTO dtoMock = mock(FuncionarioDTO.class);
        when(dtoMock.nome()).thenReturn("Nome Novo");
        when(dtoMock.cpf()).thenReturn("00000000000");
        when(dtoMock.email()).thenReturn("novo@email.com");
        when(dtoMock.telefone()).thenReturn("1199999999");
        when(dtoMock.dataNascimento()).thenReturn(LocalDate.of(1990, 1, 1));
        when(dtoMock.senha()).thenReturn("novaSenha");

        when(funciRepository.findById(id)).thenReturn(Optional.of(funcExistente));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novoHash");
        when(funciRepository.save(any(Funcionario.class))).thenAnswer(i -> i.getArgument(0));


        funcionarioService.atualizarFuncionario(id, dtoMock);

        assertEquals("Nome Novo", funcExistente.getNome());
        assertEquals("novoHash", funcExistente.getSenha());
        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    @DisplayName("Deve atualizar funcionário SEM troca de senha (Branch coverage)")
    void deveAtualizarSemSenha() {
        UUID id = UUID.randomUUID();
        Funcionario funcExistente = criarFuncionarioValido();
        funcExistente.setId(id);
        String senhaAntiga = funcExistente.getSenha();

        FuncionarioDTO dtoMock = mock(FuncionarioDTO.class);
        when(dtoMock.nome()).thenReturn("Nome Novo");
        when(dtoMock.cpf()).thenReturn("111");
        when(dtoMock.email()).thenReturn("a@a.com");
        when(dtoMock.telefone()).thenReturn("111");
        when(dtoMock.dataNascimento()).thenReturn(LocalDate.of(1980, 1, 1));

        when(dtoMock.senha()).thenReturn(null);

        when(funciRepository.findById(id)).thenReturn(Optional.of(funcExistente));
        when(funciRepository.save(any(Funcionario.class))).thenAnswer(i -> i.getArgument(0));

        funcionarioService.atualizarFuncionario(id, dtoMock);

        assertEquals(senhaAntiga, funcExistente.getSenha());
        verify(passwordEncoder, never()).encode(anyString()); // Garante que não criptografou nada
    }

    @Test
    @DisplayName("Deve falhar atualização se ID não existe")
    void deveFalharAtualizacaoIdInexistente() {
        UUID id = UUID.randomUUID();
        FuncionarioDTO dto = mock(FuncionarioDTO.class);
        when(funciRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> funcionarioService.atualizarFuncionario(id, dto));
        assertTrue(ex.getMessage().contains("Funcionário nao encontrado"));
    }

    @Test
    @DisplayName("Deve buscar por ID com sucesso")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Funcionario f = criarFuncionarioValido();
        f.setId(id);

        when(funciRepository.findById(id)).thenReturn(Optional.of(f));

        FuncionarioDTO result = funcionarioService.buscarPorId(id);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve falhar busca por ID inexistente")
    void deveFalharBuscaId() {
        UUID id = UUID.randomUUID();
        when(funciRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> funcionarioService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve deletar funcionário")
    void deveDeletar() {
        UUID id = UUID.randomUUID();
        Funcionario f = new Funcionario();
        when(funciRepository.findById(id)).thenReturn(Optional.of(f));

        funcionarioService.deletarFuncionario(id);

        verify(funciRepository).delete(f);
    }

    @Test
    @DisplayName("Deve falhar ao deletar ID inexistente")
    void deveFalharDeletar() {
        UUID id = UUID.randomUUID();
        when(funciRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> funcionarioService.deletarFuncionario(id));
    }

    @Test
    @DisplayName("Deve listar todos")
    void deveListarTodos() {
        when(funciRepository.findAll()).thenReturn(List.of(criarFuncionarioValido()));
        List<FuncionarioDTO> list = funcionarioService.getAllFuncionarios();
        assertFalse(list.isEmpty());
    }

    private Funcionario criarFuncionarioValido() {
        Funcionario f = new Funcionario();
        f.setNome("Teste da Silva");
        f.setCpf("123.456.789-00");
        f.setEmail("teste@fiap.com.br");
        f.setSenha("senhaForte");
        f.setTelefone("11999999999");
        f.setDataNascimento(LocalDate.of(1990, 1, 1)); // Maior de 18 anos
        return f;
    }
}