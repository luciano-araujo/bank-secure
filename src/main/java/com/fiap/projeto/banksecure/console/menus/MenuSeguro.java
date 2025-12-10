package com.fiap.projeto.banksecure.console.menus;

import java.util.Scanner;

public class MenuSeguro {
    // Opção 5 -> Futuramente SecureController
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
                    System.out.println("Listando seguros... (Not Implemented)\n");
                    break;
                case "2":
                    System.out.println("Criando seguro... (Not Implemented)\n");
                    break;
                case "3":
                    System.out.println("Editando seguro... (Not Implemented)\n");
                    break;
                case "4":
                    System.out.println("Excluindo seguro... (Not Implemented)\n");
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }
}
