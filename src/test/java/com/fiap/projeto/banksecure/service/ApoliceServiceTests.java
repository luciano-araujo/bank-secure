package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Cotacao;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.dto.DashboardDTO;
import com.fiap.projeto.banksecure.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApoliceServiceTests {

    @InjectMocks
    private ApoliceService apoliceService;

    @Mock
    private CotacaoService cotacaoService;

    @Mock
    private ApoliceRepository apoliceRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SeguroRepository seguroRepository;


    @Test
    @DisplayName("Deve validar cadastro com sucesso quando todos dados estão corretos")
    void deveValidarCadastroCorreto() {
        Apolice apolice = new Apolice();
        apolice.setCliente(new Cliente());
        apolice.setSeguro(new Seguro());
        apolice.setPremioFinal(BigDecimal.TEN);
        apolice.setDataInicial(LocalDate.now());
        apolice.setDataVencimento(LocalDate.now().plusYears(1));

        assertDoesNotThrow(() -> apoliceService.validarCadastro(apolice));
    }

    @Test
    @DisplayName("Deve lançar exceção se apólice for nula")
    void deveFalharApoliceNula() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> apoliceService.validarCadastro(null));
        assertEquals("Apólice é obrigatória.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se cliente ou seguro forem nulos")
    void deveFalharSemClienteOuSeguro() {
        Apolice apolice = new Apolice();

        IllegalArgumentException exCliente = assertThrows(IllegalArgumentException.class,
                () -> apoliceService.validarCadastro(apolice));
        assertEquals("A apólice deve estar atrelada a um cliente.", exCliente.getMessage());

        apolice.setCliente(new Cliente());

        IllegalArgumentException exSeguro = assertThrows(IllegalArgumentException.class,
                () -> apoliceService.validarCadastro(apolice));
        assertEquals("A apólice deve estar atrelada a um seguro.", exSeguro.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se prêmio for zero ou negativo")
    void deveFalharPremioInvalido() {
        Apolice apolice = criaraApoliceValidaBase();

        apolice.setPremioFinal(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> apoliceService.validarCadastro(apolice));

        apolice.setPremioFinal(new BigDecimal("-10.00"));
        assertThrows(IllegalArgumentException.class, () -> apoliceService.validarCadastro(apolice));
    }

    @Test
    @DisplayName("Deve lançar exceção se datas forem nulas")
    void deveFalharSemDatas() {
        Apolice apolice = criaraApoliceValidaBase();

        apolice.setDataInicial(null);
        assertEquals("Apólice deve ter data de início.",
                assertThrows(IllegalArgumentException.class, () -> apoliceService.validarCadastro(apolice)).getMessage());

        apolice.setDataInicial(LocalDate.now());
        apolice.setDataVencimento(null);
        assertEquals("Apólice deve ter data de vencimento.",
                assertThrows(IllegalArgumentException.class, () -> apoliceService.validarCadastro(apolice)).getMessage());
    }

    @Test
    @DisplayName("Deve criar apólice com sucesso integrando repositórios e serviço de cotação")
    void deveCriarApoliceComSucesso() {

        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();

        ApoliceDTO dtoEntrada = mock(ApoliceDTO.class);
        when(dtoEntrada.clienteId()).thenReturn(clienteId);
        when(dtoEntrada.seguroId()).thenReturn(seguroId);

        Apolice apoliceMock = new Apolice();
        apoliceMock.setDataInicial(LocalDate.now());
        apoliceMock.setDataVencimento(LocalDate.now().plusYears(1));
        when(dtoEntrada.toEntity()).thenReturn(apoliceMock);

        // Mocks de Banco
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        Seguro seguro = new Seguro();
        seguro.setId(seguroId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(seguroRepository.findById(seguroId)).thenReturn(Optional.of(seguro));

        // Mock da Cotação
        CotacaoDTO cotacaoDTO = new CotacaoDTO(
                UUID.randomUUID(),
                clienteId,
                seguroId,
                BigDecimal.ZERO,
                new BigDecimal("150.00"),
                LocalDate.now()
        );

        when(cotacaoService.realizarCotacao(clienteId, seguroId)).thenReturn(cotacaoDTO);

        when(apoliceRepository.save(any(Apolice.class))).thenAnswer(i -> i.getArgument(0));

        ApoliceDTO resultado = apoliceService.criarApolice(dtoEntrada);

        assertNotNull(resultado);
        verify(apoliceRepository).save(any(Apolice.class));
        verify(cotacaoService).realizarCotacao(clienteId, seguroId);

        assertEquals(new BigDecimal("150.00"), apoliceMock.getPremioFinal());
    }

    @Test
    @DisplayName("Deve falhar ao criar apólice se Cliente não existir")
    void deveFalharCriarSemCliente() {
        UUID id = UUID.randomUUID();
        ApoliceDTO dto = mock(ApoliceDTO.class);
        when(dto.clienteId()).thenReturn(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> apoliceService.criarApolice(dto));
        verifyNoInteractions(cotacaoService);
        verifyNoInteractions(apoliceRepository);
    }

    @Test
    @DisplayName("Deve renovar apólice atualizando datas e recalculando cotação")
    void deveRenovarApoliceComSucesso() {
        // Arrange
        UUID apoliceId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID seguroId = UUID.randomUUID();

        Cliente cliente = new Cliente(); cliente.setId(clienteId);
        Seguro seguro = new Seguro(); seguro.setId(seguroId);

        Apolice apoliceAntiga = new Apolice();
        apoliceAntiga.setId(apoliceId);
        apoliceAntiga.setCliente(cliente);
        apoliceAntiga.setSeguro(seguro);
        apoliceAntiga.setTotalCobertura(new BigDecimal("50000"));

        when(apoliceRepository.findById(apoliceId)).thenReturn(Optional.of(apoliceAntiga));

        CotacaoDTO novaCotacao = new CotacaoDTO(
                UUID.randomUUID(),
                clienteId,
                seguroId,
                BigDecimal.ZERO,
                new BigDecimal("200.00"),
                LocalDate.now()
        );
        when(cotacaoService.realizarCotacao(clienteId, seguroId)).thenReturn(novaCotacao);

        ArgumentCaptor<Apolice> captor = ArgumentCaptor.forClass(Apolice.class);
        when(apoliceRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        ApoliceDTO resultado = apoliceService.renovarApolice(apoliceId);

        Apolice novaApoliceSalva = captor.getValue();

        assertEquals(LocalDate.now(), novaApoliceSalva.getDataInicial(), "Data inicial deve ser hoje");
        assertEquals(LocalDate.now().plusYears(1), novaApoliceSalva.getDataVencimento(), "Vencimento deve ser daqui 1 ano");

        assertEquals(new BigDecimal("200.00"), novaApoliceSalva.getPremioFinal());

        assertEquals(cliente, novaApoliceSalva.getCliente());
        assertEquals(seguro, novaApoliceSalva.getSeguro());
    }

    @Test
    @DisplayName("Deve falhar renovação se ID não existe")
    void deveFalharRenovacaoIdInexistente() {
        UUID id = UUID.randomUUID();
        when(apoliceRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> apoliceService.renovarApolice(id));
    }

    @Test
    @DisplayName("Deve listar apólices a vencer nos próximos 30 dias")
    void deveListarApolicesAVencer() {

        when(apoliceRepository.findByDataVencimentoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        apoliceService.listarApolicesAVencer();

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(apoliceRepository).findByDataVencimentoBetween(dateCaptor.capture(), dateCaptor.capture());

        List<LocalDate> datasUsadas = dateCaptor.getAllValues();
        LocalDate hoje = LocalDate.now();

        assertEquals(hoje, datasUsadas.get(0));
        assertEquals(hoje.plusDays(30), datasUsadas.get(1));
    }

    @Test
    @DisplayName("Deve retornar dados do dashboard")
    void deveRetornarDashboard() {
        DashboardDTO dtoMock = mock(DashboardDTO.class);
        when(apoliceRepository.findDashboardPorTipoSeguro()).thenReturn(List.of(dtoMock));

        List<DashboardDTO> result = apoliceService.getDashboard();

        assertFalse(result.isEmpty());
        verify(apoliceRepository).findDashboardPorTipoSeguro();
    }

    private Apolice criaraApoliceValidaBase() {
        Apolice a = new Apolice();
        a.setCliente(new Cliente());
        a.setSeguro(new Seguro());
        a.setPremioFinal(BigDecimal.TEN);
        a.setDataInicial(LocalDate.now());
        a.setDataVencimento(LocalDate.now().plusYears(1));
        return a;
    }
}