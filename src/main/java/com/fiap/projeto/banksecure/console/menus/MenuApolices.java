package com.fiap.projeto.banksecure.console.menus;

import java.util.Scanner;

public class MenuApolices {
    // Opção 6 - Futuramente ApoliciesController
    public static void start(Scanner scanner) {
        String option;

        do {
            String menu = "\n";
            menu += "=============================\n";
            menu += "    Gerenciar Apólices\n";
            menu += "=============================\n\n";
            menu += "1. Listar Apólices\n";
            menu += "2. Criar Apólice\n";
            menu += "3. Editar Apólice\n";
            menu += "4. Excluir Apólice\n";
            menu += "0. Voltar\n\n";
            menu += "Digite a opção desejada: ";

            System.out.print(menu);
            option = scanner.nextLine();
            System.out.println();

            switch (option) {
                case "1":
                    System.out.println("Listando apólices... (Not Implemented)\n");
                    break;
                case "2":
                    System.out.println("Criando apólice... (Not Implemented)\n");
                    break;
                case "3":
                    System.out.println("Editando apólice... (Not Implemented)\n");
                    break;
                case "4":
                    System.out.println("Excluindo apólice... (Not Implemented)\n");
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
