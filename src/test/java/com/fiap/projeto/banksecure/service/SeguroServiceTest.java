package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.SeguroDTO;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeguroServiceTest {

    @InjectMocks
    private SeguroService seguroService;

    @Mock
    private SeguroRepository seguroRepository;

    // =========================================================================
    // TESTES DE CADASTRO
    // =========================================================================

    @Test
    @DisplayName("Deve cadastrar seguro com sucesso")
    void deveCadastrarSeguro() {
        // Arrange
        SeguroDTO dtoMock = mock(SeguroDTO.class);
        Seguro seguroMock = criarSeguroValido();

        when(dtoMock.toEntity()).thenReturn(seguroMock);

        when(seguroRepository.save(any(Seguro.class))).thenAnswer(i -> {
            Seguro s = i.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        // Act
        SeguroDTO resultado = seguroService.cadastrarSeguro(dtoMock);

        // Assert
        assertNotNull(resultado);
        verify(seguroRepository).save(any(Seguro.class));
    }

    // =========================================================================
    // TESTES DE ATUALIZAÇÃO (Inclui Validações de Regra de Negócio)
    // =========================================================================

    @Test
    @DisplayName("Deve atualizar seguro com sucesso")
    void deveAtualizarSeguro() {
        // Arrange
        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();
        seguroExistente.setId(id);

        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("Seguro Vida Plus");
        when(dtoMock.coberturaMinima()).thenReturn(new BigDecimal("20000"));
        when(dtoMock.valorPremioBase()).thenReturn(new BigDecimal("150.00"));

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));
        when(seguroRepository.save(any(Seguro.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        SeguroDTO resultado = seguroService.atualizarSeguro(id, dtoMock);

        // Assert
        assertNotNull(resultado);
        assertEquals("Seguro Vida Plus", seguroExistente.getTitulo());
        assertEquals(new BigDecimal("150.00"), seguroExistente.getValorPremioBase());
        verify(seguroRepository).save(seguroExistente);
    }

    @Test
    @DisplayName("Deve falhar atualização se ID não existe")
    void deveFalharAtualizacaoIdInexistente() {
        UUID id = UUID.randomUUID();
        SeguroDTO dto = mock(SeguroDTO.class);

        when(seguroRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> seguroService.atualizarSeguro(id, dto));

        assertEquals("Seguro não encontrado com o ID: " + id, ex.getMessage());
    }

    // --- Testes Específicos do método validarSeguro (chamado dentro de atualizar) ---

    @Test
    @DisplayName("Deve falhar se Título for nulo ou vazio")
    void deveFalharTituloInvalido() {
        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();

        // Mock do DTO retornando título vazio
        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("");
        // Campos obrigatórios para não falhar em outro lugar antes
        when(dtoMock.valorPremioBase()).thenReturn(BigDecimal.TEN);

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> seguroService.atualizarSeguro(id, dtoMock));

        assertEquals("Título é obrigatório.", ex.getMessage());
        verify(seguroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar se Valor Base for nulo")
    void deveFalharValorBaseNulo() {
        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();

        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("Titulo Ok");
        when(dtoMock.valorPremioBase()).thenReturn(null); // Valor Nulo

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> seguroService.atualizarSeguro(id, dtoMock));

        assertEquals("Valor de Prêmio Base é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se Valor Base for Zero ou Negativo")
    void deveFalharValorBaseInvalido() {
        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();

        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("Titulo Ok");

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));

        // Cenário 1: Zero
        when(dtoMock.valorPremioBase()).thenReturn(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> seguroService.atualizarSeguro(id, dtoMock));

        // Cenário 2: Negativo
        when(dtoMock.valorPremioBase()).thenReturn(new BigDecimal("-1.00"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> seguroService.atualizarSeguro(id, dtoMock));

        assertEquals("Valor de Prêmio Base deve ser positivo.", ex.getMessage());
    }

    // =========================================================================
    // TESTES DE BUSCA E DELEÇÃO
    // =========================================================================

    @Test
    @DisplayName("Deve buscar por ID com sucesso")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Seguro seguro = criarSeguroValido();
        seguro.setId(id);

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguro));

        SeguroDTO resultado = seguroService.buscarPorId(id);

        assertNotNull(resultado);
        verify(seguroRepository).findById(id);
    }

    @Test
    @DisplayName("Deve falhar ao buscar ID inexistente")
    void deveFalharBuscaId() {
        UUID id = UUID.randomUUID();
        when(seguroRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> seguroService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve deletar seguro com sucesso")
    void deveDeletarSeguro() {
        UUID id = UUID.randomUUID();
        Seguro seguro = criarSeguroValido();

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguro));

        seguroService.deletarSeguro(id);

        verify(seguroRepository).delete(seguro);
    }

    @Test
    @DisplayName("Deve falhar ao deletar seguro inexistente")
    void deveFalharDelecaoIdInexistente() {
        UUID id = UUID.randomUUID();
        when(seguroRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> seguroService.deletarSeguro(id));
        verify(seguroRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve listar todos")
    void deveListarTodos() {
        when(seguroRepository.findAll()).thenReturn(List.of(criarSeguroValido()));

        List<SeguroDTO> lista = seguroService.getAllSeguros();
        assertFalse(lista.isEmpty());
    }

    // =========================================================================
    // HELPER
    // =========================================================================
    private Seguro criarSeguroValido() {
        Seguro s = new Seguro();
        s.setTitulo("Seguro Auto");
        s.setCoberturaMinima(new BigDecimal("10000"));
        s.setValorPremioBase(new BigDecimal("100.00"));
        return s;
    }
}