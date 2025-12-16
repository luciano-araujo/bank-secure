package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Cliente;
import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.application.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.infra.repository.ClienteRepository;
import com.fiap.projeto.banksecure.infra.repository.CotacaoRepository;
import com.fiap.projeto.banksecure.infra.repository.SeguroRepository;
import com.fiap.projeto.banksecure.application.service.CotacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CotacaoServiceTest {

    @InjectMocks
    private CotacaoService cotacaoService;

    @Mock
    private CotacaoRepository cotacaoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SeguroRepository seguroRepository;


    @Test
    @DisplayName("Deve calcular cotação para cliente JOVEM (< 60 anos)")
    void deveCalcularCotacaoJovem() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setDataNascimento(LocalDate.now().minusYears(30));

        Seguro seguro = new Seguro();
        seguro.setId(seguroId);
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));

        CotacaoDTO resultado = cotacaoService.calcularCotacao(clienteId, seguroId);

        BigDecimal esperado = new BigDecimal("115.50");

        assertNotNull(resultado);

        assertEquals(0, esperado.compareTo(resultado.premioFinal()),
                "Cálculo incorreto para cliente jovem");
    }

    @Test
    @DisplayName("Deve calcular cotação para cliente IDOSO (> 60 anos)")
    void deveCalcularCotacaoIdoso() {

        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");


        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setDataNascimento(LocalDate.now().minusYears(65));

        Seguro seguro = new Seguro();
        seguro.setId(seguroId);
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));


        CotacaoDTO resultado = cotacaoService.calcularCotacao(clienteId, seguroId);

        BigDecimal esperado = new BigDecimal("225.50");

        assertEquals(0, esperado.compareTo(resultado.premioFinal()),
                "Cálculo incorreto para cliente idoso (deve incluir bônus)");
    }

    @Test
    @DisplayName("Deve calcular cotação na borda de exatos 60 anos (Sem bônus)")
    void deveCalcularBordaIdade() {


        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");

        Cliente cliente = new Cliente();
        cliente.setDataNascimento(LocalDate.now().minusYears(60)); // Exatos 60

        Seguro seguro = new Seguro();
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));

        CotacaoDTO resultado = cotacaoService.calcularCotacao(clienteId, seguroId);


        BigDecimal esperado = new BigDecimal("115.50");
        assertEquals(0, esperado.compareTo(resultado.premioFinal()));
    }

    @Test
    @DisplayName("Deve falhar se Cliente não existir")
    void deveFalharClienteInexistente() {
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cotacaoService.calcularCotacao(clienteId, seguroId));

        assertEquals("Cliente não encontrado", ex.getMessage());
        verifyNoInteractions(cotacaoRepository);
    }

    @Test
    @DisplayName("Deve falhar se Seguro não existir")
    void deveFalharSeguroInexistente() {
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(new Cliente()));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cotacaoService.calcularCotacao(clienteId, seguroId));

        assertEquals("Seguro não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao buscar ID inexistente")
    void deveFalharBuscaIdInexistente() {
        UUID id = UUID.randomUUID();
        when(cotacaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cotacaoService.buscarPorId(id));
    }
}