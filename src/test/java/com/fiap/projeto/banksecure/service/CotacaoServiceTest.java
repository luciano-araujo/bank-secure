package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Cotacao;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.CotacaoRepository;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    // Regras de Negócio para referência nos testes:
    // TAXA_PADRAO = 0.05 (5%)
    // BONUS_IDADE = 100.00 (se idade > 60)
    // FATOR_RISCO = 1.10 (10%)

    @Test
    @DisplayName("Deve calcular cotação para cliente JOVEM (< 60 anos)")
    void deveCalcularCotacaoJovem() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");

        // Cliente com 30 anos
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setDataNascimento(LocalDate.now().minusYears(30));

        Seguro seguro = new Seguro();
        seguro.setId(seguroId);
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));

        // Mock do save para retornar a cotação com ID gerado
        when(cotacaoRepository.save(any(Cotacao.class))).thenAnswer(i -> {
            Cotacao c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        // Act
        CotacaoDTO resultado = cotacaoService.realizarCotacao(clienteId, seguroId);

        // Assert
        /* * Lógica de Cálculo:
         * 1. Base + Taxa: 100 + (100 * 0.05) = 105.00
         * 2. Idade > 60? Não. (Mantém 105.00)
         * 3. Fator Risco: 105 * 1.10 = 115.50
         */
        BigDecimal esperado = new BigDecimal("115.50");

        assertNotNull(resultado);
        // Usamos compareTo para ignorar diferenças de escala (ex: 115.5 vs 115.50)
        assertEquals(0, esperado.compareTo(resultado.premioFinal()),
                "Cálculo incorreto para cliente jovem");

        verify(cotacaoRepository).save(any(Cotacao.class));
    }

    @Test
    @DisplayName("Deve calcular cotação para cliente IDOSO (> 60 anos)")
    void deveCalcularCotacaoIdoso() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");

        // Cliente com 65 anos
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setDataNascimento(LocalDate.now().minusYears(65));

        Seguro seguro = new Seguro();
        seguro.setId(seguroId);
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));
        when(cotacaoRepository.save(any(Cotacao.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        CotacaoDTO resultado = cotacaoService.realizarCotacao(clienteId, seguroId);

        // Assert
        /* * Lógica de Cálculo:
         * 1. Base + Taxa: 100 + (100 * 0.05) = 105.00
         * 2. Idade > 60? Sim. Soma 100.00 -> 105 + 100 = 205.00
         * 3. Fator Risco: 205 * 1.10 = 225.50
         */
        BigDecimal esperado = new BigDecimal("225.50");

        assertEquals(0, esperado.compareTo(resultado.premioFinal()),
                "Cálculo incorreto para cliente idoso (deve incluir bônus)");
    }

    @Test
    @DisplayName("Deve calcular cotação na borda de exatos 60 anos (Sem bônus)")
    void deveCalcularBordaIdade() {
        // A regra diz: if (idade > IDADE_BONUS). Se for == 60, não entra no if.

        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();
        BigDecimal valorBase = new BigDecimal("100.00");

        Cliente cliente = new Cliente();
        cliente.setDataNascimento(LocalDate.now().minusYears(60)); // Exatos 60

        Seguro seguro = new Seguro();
        seguro.setValorPremioBase(valorBase);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));
        when(cotacaoRepository.save(any(Cotacao.class))).thenAnswer(i -> i.getArgument(0));

        CotacaoDTO resultado = cotacaoService.realizarCotacao(clienteId, seguroId);

        // Esperado igual ao jovem (115.50)
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
                () -> cotacaoService.realizarCotacao(clienteId, seguroId));

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
                () -> cotacaoService.realizarCotacao(clienteId, seguroId));

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