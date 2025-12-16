package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.infra.repository.SeguroRepository;
import com.fiap.projeto.banksecure.application.service.SeguroService;
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

        SeguroDTO resultado = seguroService.cadastrarSeguro(dtoMock);

        assertNotNull(resultado);
        verify(seguroRepository).save(any(Seguro.class));
    }


    @Test
    @DisplayName("Deve atualizar seguro com sucesso")
    void deveAtualizarSeguro() {

        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();
        seguroExistente.setId(id);

        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("Seguro Vida Plus");
        when(dtoMock.coberturaMinima()).thenReturn("20000");
        when(dtoMock.valorPremioBase()).thenReturn(new BigDecimal("150.00"));

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));
        when(seguroRepository.save(any(Seguro.class))).thenAnswer(i -> i.getArgument(0));

        SeguroDTO resultado = seguroService.atualizarSeguro(id, dtoMock);

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

    @Test
    @DisplayName("Deve falhar se Título for nulo ou vazio")
    void deveFalharTituloInvalido() {
        UUID id = UUID.randomUUID();
        Seguro seguroExistente = criarSeguroValido();

        SeguroDTO dtoMock = mock(SeguroDTO.class);
        when(dtoMock.titulo()).thenReturn("");

        when(dtoMock.valorPremioBase()).thenReturn(BigDecimal.TEN);

        when(seguroRepository.findById(id)).thenReturn(Optional.of(seguroExistente));

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


        when(dtoMock.valorPremioBase()).thenReturn(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> seguroService.atualizarSeguro(id, dtoMock));


        when(dtoMock.valorPremioBase()).thenReturn(new BigDecimal("-1.00"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> seguroService.atualizarSeguro(id, dtoMock));

        assertEquals("Valor de Prêmio Base deve ser positivo.", ex.getMessage());
    }


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


    private Seguro criarSeguroValido() {
        Seguro s = new Seguro();
        s.setTitulo("Seguro Auto");
        s.setCoberturaMinima("10000");
        s.setValorPremioBase(new BigDecimal("100.00"));
        return s;
    }
}