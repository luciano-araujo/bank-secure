package com.fiap.projeto.banksecure.infra.repository.console.menus;

import com.fiap.projeto.banksecure.application.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.application.dto.ClienteDTO;
import com.fiap.projeto.banksecure.application.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.application.service.ApoliceService;
import com.fiap.projeto.banksecure.application.service.ClienteService;
import com.fiap.projeto.banksecure.application.service.CotacaoService;
import com.fiap.projeto.banksecure.application.service.SeguroService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class MenuApolices {

    public void start(Scanner scanner,
                     ApoliceService apoliceService,
                     ClienteService clienteService,
                     SeguroService seguroService,
                     CotacaoService cotacaoService) {
        String option;

        do {
            String menu = "\n";
            menu += "=============================\n";
            menu += "    Gerenciar Apolices\n";
            menu += "=============================\n\n";
            menu += "1. Listar Apolices\n";
            menu += "2. Registrar Apolice (com cotacao)\n";
            menu += "3. Apolices a Vencer\n";
            menu += "4. Renovar Apolice\n";
            menu += "0. Voltar\n\n";
            menu += "Digite a opcao desejada: ";

            System.out.print(menu);
            option = scanner.nextLine();
            System.out.println();

            switch (option) {
                case "1":
                    listarApolices(apoliceService);
                    break;
                case "2":
                    registrarApolice(scanner, apoliceService, clienteService, seguroService, cotacaoService);
                    break;
                case "3":
                    listarApolicesAVencer(apoliceService);
                    break;
                case "4":
                    renovarApolice(scanner, apoliceService);
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opcao invalida.\n");
            }

        } while (!option.equals("0"));
    }

    private void listarApolices(ApoliceService apoliceService) {
        List<ApoliceDTO> apoliceList = apoliceService.listarTodasApolices();

        if (apoliceList.isEmpty()) {
            System.out.println("Nenhuma apolice cadastrada.");
            return;
        }

        apoliceList.forEach(a -> {
            System.out.println("ID: " + a.id());
            System.out.println("Cliente ID: " + a.clienteId());
            System.out.println("Seguro ID: " + a.seguroId());
            System.out.println("Premio Final: " + a.premioFinal());
            System.out.println("Total Cobertura: " + a.totalCobertura());
            System.out.println("Data Inicial: " + a.dataInicial());
            System.out.println("Data Vencimento: " + a.dataVencimento());
            System.out.println("---------------------------");
        });
    }

    private void registrarApolice(Scanner scanner,
                                  ApoliceService apoliceService,
                                  ClienteService clienteService,
                                  SeguroService seguroService,
                                  CotacaoService cotacaoService) {
        var clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.\n");
            return;
        }
        var seguros = seguroService.getAllSeguros();
        if (seguros.isEmpty()) {
            System.out.println("Nenhum seguro cadastrado.\n");
            return;
        }

        ClienteDTO cliente = escolherCliente(scanner, clientes);
        if (cliente == null) return;

        SeguroDTO seguro = escolherSeguro(scanner, seguros);
        if (seguro == null) return;

        System.out.print("Informe o total de cobertura: ");
        BigDecimal totalCobertura;
        try {
            totalCobertura = new BigDecimal(scanner.nextLine().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido para cobertura.\n");
            return;
        }

        CotacaoDTO cotacao = cotacaoService.calcularCotacao(cliente.id(), seguro.id());
        System.out.printf("Cotacao realizada. Premio final: %s (cliente: %s, seguro: %s)%n",
                cotacao.premioFinal(), cliente.nome(), seguro.titulo());
        System.out.print("Deseja registrar a apolice? (s/n): ");
        String confirmar = scanner.nextLine().trim();
        if (!confirmar.equalsIgnoreCase("s")) {
            System.out.println("Apolice nao criada.\n");
            return;
        }

        ApoliceDTO nova = new ApoliceDTO(
                null,
                cliente.id(),
                totalCobertura,
                cotacao.premioFinal(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                seguro.id()
        );

        ApoliceDTO criada = apoliceService.criarApolice(nova);
        System.out.println("Apolice criada com sucesso. ID: " + criada.id() + "\n");
    }

    private ClienteDTO escolherCliente(Scanner scanner, List<ClienteDTO> clientes) {
        System.out.println("Clientes:");
        for (int i = 0; i < clientes.size(); i++) {
            ClienteDTO c = clientes.get(i);
            System.out.printf("%d) %s | CPF: %s%n", i + 1, c.nome(), c.cpf());
        }
        System.out.print("Escolha o numero do cliente: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= clientes.size()) {
                System.out.println("Cliente inexistente.\n");
                return null;
            }
            return clientes.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.\n");
            return null;
        }
    }

    private SeguroDTO escolherSeguro(Scanner scanner, List<SeguroDTO> seguros) {
        System.out.println("Seguros:");
        for (int i = 0; i < seguros.size(); i++) {
            SeguroDTO s = seguros.get(i);
            System.out.printf("%d) %s | Cobertura minima: %s | Premio base: %s%n",
                    i + 1, s.titulo(), s.coberturaMinima(), s.valorPremioBase());
        }
        System.out.print("Escolha o numero do seguro: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= seguros.size()) {
                System.out.println("Seguro inexistente.\n");
                return null;
            }
            return seguros.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.\n");
            return null;
        }
    }

    private void listarApolicesAVencer(ApoliceService apoliceService) {
        List<ApoliceDTO> apoliceList = apoliceService.listarApolicesAVencer();

        if (apoliceList.isEmpty()) {
            System.out.println("Nenhuma apolice a vencer nos proximos 30 dias.");
            return;
        }

        System.out.println("Apolices a vencer nos proximos 30 dias:\n");
        apoliceList.forEach(a -> {
            System.out.println("ID: " + a.id());
            System.out.println("Cliente ID: " + a.clienteId());
            System.out.println("Seguro ID: " + a.seguroId());
            System.out.println("Data Vencimento: " + a.dataVencimento());
            System.out.println("---------------------------");
        });
    }

    private void renovarApolice(Scanner scanner, ApoliceService apoliceService) {
        try {
            System.out.print("ID da apolice a renovar: ");
            String idStr = scanner.nextLine();
            UUID id = UUID.fromString(idStr);

            ApoliceDTO apoliceRenovada = apoliceService.renovarApolice(id);

            System.out.println("Apolice renovada com sucesso!");
            System.out.println("Nova data de vencimento: " + apoliceRenovada.dataVencimento());
            System.out.println("Premio final: " + apoliceRenovada.premioFinal() + "\n");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao renovar apolice: ID invalido\n");
        } catch (Exception e) {
            System.out.println("Erro ao renovar apolice: " + e.getMessage() + "\n");
        }
    }
}
