package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.enums.TipoUsuarioEnum;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
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

    // =========================================================================
    // CENÁRIO 1: LOGIN CLIENTE
    // =========================================================================

    @Test
    @DisplayName("Deve realizar login de CLIENTE com sucesso")
    void deveLogarClienteComSucesso() {
        // Arrange
        String email = "cliente@teste.com";
        String senhaPura = "123456";
        String senhaHash = "hash123";

        // Mock do Request (Assumindo Record)
        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);
        when(request.senha()).thenReturn(senhaPura);

        // Mock da Entidade Cliente
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("Cliente Teste");
        cliente.setSenha(senhaHash);

        // Comportamento: Acha no ClienteRepository
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        // Comportamento: Senha confere
        when(passwordEncoder.matches(senhaPura, senhaHash)).thenReturn(true);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.authenticated()); // Assumindo campo 'sucesso' no record response
        assertEquals(TipoUsuarioEnum.CLIENTE, response.tipoUsuario()); // Assumindo campo 'tipoUsuario'

        // Verifica que NEM tentou buscar no repositório de funcionário (Short-circuit)
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

        assertEquals("Senha inválida", ex.getMessage());
    }

    // =========================================================================
    // CENÁRIO 2: LOGIN FUNCIONÁRIO
    // =========================================================================

    @Test
    @DisplayName("Deve realizar login de FUNCIONÁRIO com sucesso")
    void deveLogarFuncionarioComSucesso() {
        // Arrange
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

        // 1. Não acha no Cliente
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());
        // 2. Acha no Funcionário
        when(funcionarioRepository.findByEmail(email)).thenReturn(Optional.of(funcionario));
        // 3. Senha confere
        when(passwordEncoder.matches(senhaPura, senhaHash)).thenReturn(true);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
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

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Senha inválida", ex.getMessage());
    }

    // =========================================================================
    // CENÁRIO 3: USUÁRIO NÃO ENCONTRADO
    // =========================================================================

    @Test
    @DisplayName("Deve falhar quando email não existe em nenhuma base")
    void deveFalharUsuarioNaoEncontrado() {
        // Arrange
        String email = "ninguem@teste.com";
        AuthRequest request = mock(AuthRequest.class);
        when(request.email()).thenReturn(email);

        // Não acha em nenhum lugar
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(funcionarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}