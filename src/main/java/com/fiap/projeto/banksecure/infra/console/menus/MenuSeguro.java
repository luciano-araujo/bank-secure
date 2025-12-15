package com.fiap.projeto.banksecure.infra.console.menus;

import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.application.service.SeguroService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
public class MenuSeguro {

    public void start(Scanner scanner, SeguroService seguroService) {
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
                    listarSeguros(seguroService);
                    break;
                case "2":
                    cadastrarSeguro(scanner, seguroService);
                    break;
                case "3":
                    editarSeguro(scanner, seguroService);
                    break;
                case "4":
                    excluirSeguro(scanner, seguroService);
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }

    private void listarSeguros(SeguroService seguroService) {
        List<SeguroDTO> seguroList = seguroService.getAllSeguros();

        if (seguroList.isEmpty()) {
            System.out.println("Nenhum seguro cadastrado.");
            return;
        }

        seguroList.forEach(s -> {
            System.out.println("ID: " + s.id());
            System.out.println("Título: " + s.titulo());
            System.out.println("Cobertura Mínima: " + s.coberturaMinima());
            System.out.println("Valor Prêmio Base: " + s.valorPremioBase());
            System.out.println("---------------------------");
        });
    }

    private void cadastrarSeguro(Scanner scanner, SeguroService seguroService) {
        try {
            System.out.print("Título do seguro: ");
            String titulo = scanner.nextLine();

            System.out.print("Cobertura mínima: ");
            String coberturaMinima = scanner.nextLine();

            System.out.print("Valor do prêmio base: ");
            String valorStr = scanner.nextLine().replace(",", ".");
            BigDecimal valorPremioBase = new BigDecimal(valorStr);

            Seguro seguro = new Seguro();
            seguro.setTitulo(titulo);
            seguro.setCoberturaMinima(coberturaMinima);
            seguro.setValorPremioBase(valorPremioBase);

            seguroService.cadastrarSeguro(new SeguroDTO(null, titulo, coberturaMinima, valorPremioBase));

            System.out.println("Seguro cadastrado com sucesso!\n");
        } catch (NumberFormatException e) {
            System.out.println("Erro ao cadastrar seguro: Formato de número inválido\n");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar seguro: " + e.getMessage() + "\n");
        }
    }

    private void editarSeguro(Scanner scanner, SeguroService seguroService) {
        try {
            System.out.print("ID do seguro a editar: ");
            String idStr = scanner.nextLine();
            java.util.UUID id = java.util.UUID.fromString(idStr);

            SeguroDTO seguroExistente = seguroService.buscarPorId(id);

            System.out.print("Novo título do seguro: ");
            String titulo = scanner.nextLine();

            System.out.print("Nova cobertura mínima: ");
            String coberturaMinima = scanner.nextLine();

            System.out.print("Novo valor do prêmio base: ");
            String valorStr = scanner.nextLine().replace(",", ".");
            BigDecimal valorPremioBase = new BigDecimal(valorStr);

            SeguroDTO seguroAtualizado = new SeguroDTO(id, titulo, coberturaMinima, valorPremioBase);
            seguroService.atualizarSeguro(id, seguroAtualizado);

            System.out.println("Seguro editado com sucesso!\n");

        } catch (NumberFormatException e) {
            System.out.println("Erro ao editar seguro: Formato de número inválido\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao editar seguro: ID inválido\n");
        } catch (Exception e) {
            System.out.println("Erro ao editar seguro: " + e.getMessage() + "\n");
        }
    }

    private void excluirSeguro(Scanner scanner, SeguroService seguroService) {
        try {
            System.out.print("ID do seguro a excluir: ");
            String idStr = scanner.nextLine();
            java.util.UUID id = java.util.UUID.fromString(idStr);

            seguroService.deletarSeguro(id);

            System.out.println("Seguro excluído com sucesso!\n");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao excluir seguro: ID inválido\n");
        } catch (Exception e) {
            System.out.println("Erro ao excluir seguro: " + e.getMessage() + "\n");
        }
    }
}
