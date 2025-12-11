package com.fiap.projeto.banksecure.console.menus;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MenuCliente {
    // Opção 4 -> Futuramente ClientController
    public void start(Scanner scanner) {
        String option;

        do {
            String menu = "\n";
            menu += "=============================\n";
            menu += "   Gerenciar Clientes\n";
            menu += "=============================\n\n";
            menu += "1. Listar Clientes\n";
            menu += "2. Criar Cliente\n";
            menu += "3. Editar Cliente\n";
            menu += "4. Excluir Cliente\n";
            menu += "0. Voltar\n\n";
            menu += "Digite a opção desejada: ";

            System.out.print(menu);
            option = scanner.nextLine();
            System.out.println();

            switch (option) {
                case "1":
                    System.out.println("Listando clientes... (Not Implemented)\n");
                    break;
                case "2":
                    System.out.println("Criando cliente... (Not Implemented)\n");
                    break;
                case "3":
                    System.out.println("Editando cliente... (Not Implemented)\n");
                    break;
                case "4":
                    System.out.println("Excluindo cliente... (Not Implemented)\n");
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }

    // Todo: opções separadas
}
