package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Cliente;
import com.fiap.projeto.banksecure.domain.entity.Funcionario;
import com.fiap.projeto.banksecure.application.dto.AuthRequest;
import com.fiap.projeto.banksecure.application.dto.AuthResponse;
import com.fiap.projeto.banksecure.domain.enums.TipoUsuarioEnum;
import com.fiap.projeto.banksecure.infra.repository.ClienteRepository;
import com.fiap.projeto.banksecure.infra.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.application.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve realizar login de CLIENTE com sucesso")
    void deveLogarClienteComSucesso() {
        String email = "cliente@teste.com";
        String senhaPura = "123456";
        String senhaHash = "hash123";

        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);
        when(request.senha()).thenReturn(senhaPura);

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("Cliente Teste");
        cliente.setSenha(senhaHash);

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        when(passwordEncoder.matches(senhaPura, senhaHash)).thenReturn(true);


        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertTrue(response.authenticated());
        assertEquals(TipoUsuarioEnum.CLIENTE, response.tipoUsuario());

        verify(funcionarioRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve falhar login de CLIENTE com senha incorreta")
    void deveFalharLoginClienteSenhaErrada() {
        // Arrange
        String email = "cliente@teste.com";
        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);
        when(request.senha()).thenReturn("senhaErrada");

        Cliente cliente = new Cliente();
        cliente.setSenha("hashCorreto");

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", "hashCorreto")).thenReturn(false);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Senha invalida", ex.getMessage());
    }

    @Test
    @DisplayName("Deve realizar login de FUNCIONÁRIO com sucesso")
    void deveLogarFuncionarioComSucesso() {
        String email = "func@teste.com";
        String senhaPura = "admin123";
        String senhaHash = "hashAdmin";

        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);
        when(request.senha()).thenReturn(senhaPura);

        Funcionario funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());
        funcionario.setNome("Funcionario Teste");
        funcionario.setSenha(senhaHash);


        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

        when(funcionarioRepository.findByEmail(email)).thenReturn(Optional.of(funcionario));

        when(passwordEncoder.matches(senhaPura, senhaHash)).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(TipoUsuarioEnum.FUNCIONARIO, response.tipoUsuario());
    }

    @Test
    @DisplayName("Deve falhar login de FUNCIONÁRIO com senha incorreta")
    void deveFalharLoginFuncionarioSenhaErrada() {
        // Arrange
        String email = "func@teste.com";
        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);
        when(request.senha()).thenReturn("errada");

        Funcionario funcionario = new Funcionario();
        funcionario.setSenha("hash");

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(funcionarioRepository.findByEmail(email)).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Senha invalida", ex.getMessage());
    }


    @Test
    @DisplayName("Deve falhar quando email não existe em nenhuma base")
    void deveFalharUsuarioNaoEncontrado() {

        String email = "ninguem@teste.com";
        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(funcionarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Usuario nao encontrado", ex.getMessage());
    }
}