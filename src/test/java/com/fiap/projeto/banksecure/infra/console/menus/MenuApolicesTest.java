package com.fiap.projeto.banksecure.infra.console.menus;

import com.fiap.projeto.banksecure.application.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.application.dto.ClienteDTO;
import com.fiap.projeto.banksecure.application.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.application.service.ApoliceService;
import com.fiap.projeto.banksecure.application.service.ClienteService;
import com.fiap.projeto.banksecure.application.service.CotacaoService;
import com.fiap.projeto.banksecure.application.service.SeguroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuApolicesTest {

    private MenuApolices menuApolices;

    @Mock
    private ApoliceService apoliceService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private SeguroService seguroService;

    @Mock
    private CotacaoService cotacaoService;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        menuApolices = new MenuApolices();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void deveListarApolicesComSucesso() {
        UUID id = UUID.randomUUID();
        ApoliceDTO apolice = new ApoliceDTO(id, UUID.randomUUID(), new BigDecimal("50000"),
                new BigDecimal("500"), LocalDate.now(), LocalDate.now().plusYears(1), UUID.randomUUID());
        when(apoliceService.listarTodasApolices()).thenReturn(List.of(apolice));

        Scanner scanner = createScanner("1\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        String output = outputStream.toString();
        assertTrue(output.contains("ID: " + id));
        verify(apoliceService).listarTodasApolices();
    }

    @Test
    void deveMostrarMensagemQuandoNenhumaApoliceCadastrada() {
        when(apoliceService.listarTodasApolices()).thenReturn(Collections.emptyList());

        Scanner scanner = createScanner("1\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Nenhuma apolice cadastrada."));
    }


    @Test
    void naoDeveRegistrarApoliceSeNaoHouverClientes() {
        when(clienteService.getAllClientes()).thenReturn(Collections.emptyList());

        Scanner scanner = createScanner("2\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Nenhum cliente cadastrado."));
    }


    @Test
    void deveListarApolicesAVencerComSucesso() {
        UUID id = UUID.randomUUID();
        ApoliceDTO apolice = new ApoliceDTO(id, UUID.randomUUID(), new BigDecimal("50000"),
                new BigDecimal("500"), LocalDate.now(), LocalDate.now().plusDays(15), UUID.randomUUID());
        when(apoliceService.listarApolicesAVencer()).thenReturn(List.of(apolice));

        Scanner scanner = createScanner("3\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Apolices a vencer nos proximos 30 dias"));
        verify(apoliceService).listarApolicesAVencer();
    }

    @Test
    void deveMostrarMensagemQuandoNenhumaApoliceAVencer() {
        when(apoliceService.listarApolicesAVencer()).thenReturn(Collections.emptyList());

        Scanner scanner = createScanner("3\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Nenhuma apolice a vencer nos proximos 30 dias."));
    }

    @Test
    void deveRenovarApoliceComSucesso() {
        UUID id = UUID.randomUUID();
        ApoliceDTO apoliceRenovada = new ApoliceDTO(id, UUID.randomUUID(), new BigDecimal("50000"),
                new BigDecimal("500"), LocalDate.now(), LocalDate.now().plusYears(1), UUID.randomUUID());
        when(apoliceService.renovarApolice(id)).thenReturn(apoliceRenovada);

        Scanner scanner = createScanner("4\n" + id + "\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Apolice renovada com sucesso!"));
        verify(apoliceService).renovarApolice(id);
    }

    @Test
    void deveMostrarErroAoRenovarApoliceComIdInvalido() {
        Scanner scanner = createScanner("4\ninvalido\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Erro ao renovar apolice: ID invalido"));
    }

    @Test
    void deveMostrarOpcaoInvalida() {
        Scanner scanner = createScanner("9\n0\n");
        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);

        assertTrue(outputStream.toString().contains("Opcao invalida."));
    }

}
