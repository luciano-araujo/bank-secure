package com.fiap.projeto.banksecure.infra.console;

import com.fiap.projeto.banksecure.application.dto.AuthRequest;
import com.fiap.projeto.banksecure.application.dto.AuthResponse;
import com.fiap.projeto.banksecure.application.dto.DashboardDTO;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.application.service.*;
import com.fiap.projeto.banksecure.infra.console.menus.MenuApolices;
import com.fiap.projeto.banksecure.infra.console.menus.MenuCliente;
import com.fiap.projeto.banksecure.infra.console.menus.MenuSeguro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleMenuTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private FuncionarioService funcionarioService;

    @Mock
    private SeguroService seguroService;

    @Mock
    private CotacaoService cotacaoService;

    @Mock
    private ApoliceService apoliceService;

    @Mock
    private AuthService authService;

    @Mock
    private MenuCliente menuCliente;

    @Mock
    private MenuApolices menuApolices;

    @Mock
    private MenuSeguro menuSeguro;

    @InjectMocks
    private ConsoleMenu consoleMenu;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Deve sair do menu quando opcao 0 for selecionada")
    void deveSairDoMenuQuandoOpcaoZero() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleMenu.start();

        assertTrue(outputStream.toString().contains("Saindo..."));
    }

    @Test
    @DisplayName("Deve falhar login com credenciais invalidas")
    void deveFalharLoginComCredenciaisInvalidas() {
        String input = "1\nemail@invalido.com\nsenhaerrada\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        consoleMenu.start();

        assertTrue(outputStream.toString().contains("Email ou senha invalidos"));
    }

    @Test
    @DisplayName("Deve cadastrar funcionario com sucesso")
    void deveCadastrarFuncionarioComSucesso() {
        String input = "2\nNome Teste\n12345678900\ntest@email.com\n11999999999\nsenha123\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleMenu.start();

        verify(funcionarioService, atLeastOnce()).cadastrarFuncionario(any());
        assertTrue(outputStream.toString().contains("Funcionario cadastrado com sucesso"));
    }

    @Test
    @DisplayName("Deve mostrar mensagem quando nao ha seguros cadastrados")
    void deveMostrarMensagemQuandoNaoHaSeguros() {
        String input = "3\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(seguroService.getAllSeguros()).thenReturn(Collections.emptyList());

        consoleMenu.start();

        assertTrue(outputStream.toString().contains("Nenhum seguro cadastrado"));
    }

    @Test
    @DisplayName("Deve bloquear opcao 4 quando usuario nao logado")
    void deveBloquearOpcao4QuandoNaoLogado() {
        String input = "4\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleMenu.start();

        assertTrue(outputStream.toString().contains("Opcao invalida"));
        verify(menuCliente, never()).start(any(), any());
    }

    @Test
    @DisplayName("Deve mostrar opcao invalida para entrada desconhecida")
    void deveMostrarOpcaoInvalidaParaEntradaDesconhecida() {
        String input = "99\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleMenu.start();

        assertTrue(outputStream.toString().contains("Opcao invalida"));
    }

}
