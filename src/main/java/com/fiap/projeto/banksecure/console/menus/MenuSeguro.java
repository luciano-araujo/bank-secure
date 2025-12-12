package com.fiap.projeto.banksecure.console.menus;

import com.fiap.projeto.banksecure.dto.SeguroDTO;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import com.fiap.projeto.banksecure.service.SeguroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MenuSeguro {
    private final SeguroService seguroService;

    public void start(Scanner scanner) {
        String option;

        do {
            String menu = "\n";
            menu += "=============================\n";
            menu += "    Gerenciar Seguros\n";
            menu += "=============================\n\n";
            menu += "1. Listar Seguros\n";
            menu += "2. Criar Seguro\n";
            menu += "3. Editar Seguro\n";
            menu += "4. Excluir Seguro\n";
            menu += "0. Voltar\n\n";
            menu += "Digite a opção desejada: ";

            System.out.print(menu);
            option = scanner.nextLine();
            System.out.println();

            switch (option) {
                case "1":
                    listarSeguros();
                    break;
                case "2":
                    cadastrarSeguro(scanner);
                    break;
                case "3":
                    editarSeguro(scanner);
                    break;
                case "4":
                    excluirSeguro(scanner);
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }

    private void listarSeguros() {
        List<SeguroDTO> seguros = seguroService.getAllSeguros();
        if (seguros.isEmpty()) {
            System.out.println("Nenhum seguro cadastrado.\n");
            return;
        }

        seguros.forEach(s -> {
            System.out.println("ID: " + s.id());
            System.out.println("Título: " + s.titulo());
            System.out.println("Valor Prêmio Base: " + s.valorPremioBase());
            System.out.println("Cobertura Mínima: " + s.coberturaMinima());
            System.out.println("Descrição: " + s.descricao());
            System.out.println("Tipo: " + s.tipoSeguroEnum());
            System.out.println("---------------------------");
        });
    }

    private void cadastrarSeguro(Scanner scanner) {
        try {
            System.out.print("Título: ");
            String titulo = scanner.nextLine();

            System.out.print("Valor do prêmio base: ");
            String valorStr = scanner.nextLine();
            BigDecimal valorPremioBase;
            try {
                valorPremioBase = parseBigDecimal(valorStr);
            } catch (NumberFormatException nfe) {
                System.out.println("Formato de valor inválido. Use números, ex: 1234.56 ou 1.234,56\n");
                return;
            }

            System.out.print("Cobertura mínima: ");
            String coberturaMinima = scanner.nextLine();

            System.out.print("Descrição: ");
            String descricao = scanner.nextLine();

            System.out.println("Tipos disponíveis:");
            for (TipoSeguroEnum t : TipoSeguroEnum.values()) {
                System.out.println("- " + t.name());
            }
            System.out.print("Tipo do seguro (digite o nome exato): ");
            String tipoStr = scanner.nextLine();
            TipoSeguroEnum tipo = null;
            try {
                tipo = TipoSeguroEnum.valueOf(tipoStr);
            } catch (IllegalArgumentException iae) {
                System.out.println("Tipo de seguro inválido. Use um dos tipos listados.\n");
                return;
            }

            SeguroDTO dto = new SeguroDTO(null, titulo, valorPremioBase, coberturaMinima, descricao, tipo, List.of());
            seguroService.cadastrarSeguro(dto);

            System.out.println("Seguro cadastrado com sucesso!\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar seguro: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar seguro: formato de valor ou tipo inválido.\n");
        }
    }

    private void editarSeguro(Scanner scanner) {
        try {
            System.out.print("ID do seguro a editar: ");
            String idStr = scanner.nextLine();
            UUID id = UUID.fromString(idStr);

            System.out.print("Novo título (enter para manter): ");
            String titulo = scanner.nextLine();
            if (titulo.isBlank()) titulo = null;

            System.out.print("Novo valor do prêmio base (enter para manter): ");
            String valorStr = scanner.nextLine();
            BigDecimal valorPremioBase = null;
            if (!valorStr.isBlank()) {
                try {
                    valorPremioBase = parseBigDecimal(valorStr);
                } catch (NumberFormatException nfe) {
                    System.out.println("Formato de valor inválido. Use números, ex: 1234.56 ou 1.234,56\n");
                    return;
                }
            }

            System.out.print("Nova cobertura mínima (enter para manter): ");
            String coberturaMinima = scanner.nextLine();
            if (coberturaMinima.isBlank()) coberturaMinima = null;

            System.out.print("Nova descrição (enter para manter): ");
            String descricao = scanner.nextLine();
            if (descricao.isBlank()) descricao = null;

            System.out.print("Novo tipo do seguro (enter para manter): ");
            String tipoStr = scanner.nextLine();
            tipoStr = tipoStr.trim();
            TipoSeguroEnum tipo;
            if (tipoStr.isBlank()) {
                tipo = null;
            } else {
                try {
                    tipo = TipoSeguroEnum.valueOf(tipoStr.trim());
                } catch (IllegalArgumentException iae) {
                    System.out.println("Tipo de seguro inválido. Use um dos tipos listados.\n");
                    return;
                }
            }

            SeguroDTO dto = new SeguroDTO(null, titulo, valorPremioBase, coberturaMinima, descricao, tipo, null);
            seguroService.atualizarSeguro(id, dto);

            System.out.println("Seguro editado com sucesso!\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao editar seguro: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("Erro ao editar seguro: formato de ID ou valor inválido.\n");
        }
    }

    private void excluirSeguro(Scanner scanner) {
        try {
            System.out.print("ID do seguro a excluir: ");
            String idStr = scanner.nextLine();
            UUID id = UUID.fromString(idStr);

            seguroService.deletarSeguro(id);

            System.out.println("Seguro excluído com sucesso!\n");
        } catch (Exception e) {
            System.out.println("Erro ao excluir seguro: " + e.getMessage() + "\n");
        }
    }

    private static BigDecimal parseBigDecimal(String input) {
        if (input == null) throw new NumberFormatException("Entrada nula");
        String s = input.trim().replaceAll("\\s+", "");
        if (s.isEmpty()) throw new NumberFormatException("String vazia");
        // aceita formatos com ponto ou vírgula como separador decimal
        // remove ponto quando houver vírgula
        if (s.contains(",")) {
            s = s.replace(".", "");
            s = s.replace(",", ".");
        }
        // também remove possíveis sinais de moeda e espaços
        s = s.replace("$", "").replace("R$", "");
        return new BigDecimal(s);
    }
}