package com.fiap.projeto.banksecure.console.menus;

import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.service.ApoliceService;
import com.fiap.projeto.banksecure.service.ClienteService;
import com.fiap.projeto.banksecure.service.SeguroService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class MenuApolices {

    public void start(Scanner scanner, ApoliceService apoliceService, ClienteService clienteService, SeguroService seguroService) {
        String option;

        do {
            String menu = "\n";
            menu += "=============================\n";
            menu += "    Gerenciar Apólices\n";
            menu += "=============================\n\n";
            menu += "1. Listar Apólices\n";
            menu += "2. Apólices a Vencer\n";
            menu += "3. Renovar Apólice\n";
            menu += "0. Voltar\n\n";
            menu += "Digite a opção desejada: ";

            System.out.print(menu);
            option = scanner.nextLine();
            System.out.println();

            switch (option) {
                case "1":
                    listarApolices(apoliceService);
                    break;
                case "2":
                    listarApolicesAVencer(apoliceService);
                    break;
                case "3":
                    renovarApolice(scanner, apoliceService);
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }

    private void listarApolices(ApoliceService apoliceService) {
        List<ApoliceDTO> apoliceList = apoliceService.listarTodasApolices();

        if (apoliceList.isEmpty()) {
            System.out.println("Nenhuma apólice cadastrada.");
            return;
        }

        apoliceList.forEach(a -> {
            System.out.println("ID: " + a.id());
            System.out.println("Cliente ID: " + a.clienteId());
            System.out.println("Seguro ID: " + a.seguroId());
            System.out.println("Prêmio Final: " + a.totalCobertura());
            System.out.println("Data Inicial: " + a.dataInicial());
            System.out.println("Data Vencimento: " + a.dataVencimento());
            System.out.println("---------------------------");
        });
    }

    private void listarApolicesAVencer(ApoliceService apoliceService) {
        List<ApoliceDTO> apoliceList = apoliceService.listarApolicesAVencer();

        if (apoliceList.isEmpty()) {
            System.out.println("Nenhuma apólice a vencer nos próximos 30 dias.");
            return;
        }

        System.out.println("Apólices a vencer nos próximos 30 dias:\n");
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
            System.out.print("ID da apólice a renovar: ");
            String idStr = scanner.nextLine();
            UUID id = UUID.fromString(idStr);

            ApoliceDTO apoliceRenovada = apoliceService.renovarApolice(id);

            System.out.println("Apólice renovada com sucesso!");
            System.out.println("Nova data de vencimento: " + apoliceRenovada.dataVencimento() + "\n");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao renovar apólice: ID inválido\n");
        } catch (Exception e) {
            System.out.println("Erro ao renovar apólice: " + e.getMessage() + "\n");
        }
    }
}

