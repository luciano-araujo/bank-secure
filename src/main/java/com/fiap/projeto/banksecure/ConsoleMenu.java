package com.fiap.projeto.banksecure;

import lombok.Getter;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    public static void main(String[] args) {
        String option;
        Scanner scanner = new Scanner(System.in);
        LoginStats loginStats = LoginStats.ANONIMO;

        do {
            String menu = getMenu(loginStats);

            System.out.print(menu);

            option = scanner.nextLine();

            System.out.println();

            switch (option){
                case "1":
                    loginStats = loginOption(scanner, loginStats);
                    break;
                case "2":
                    CadastroOption();
                    break;
                case "3":
                    SecureTypesViewOption();
                    break;
                case "4":
                    if (loginValidator(loginStats)) {
                        ManageClientsOption();
                    }
                    break;
                case "5":
                    if (loginValidator(loginStats)) {
                        ManageSecuresOption();
                    }
                    break;
                case "6":
                    if (loginValidator(loginStats)) {
                        ManageApoliciesOption();
                    }
                    break;
                case "7":
                    if (loginValidator(loginStats)) {
                        ViewDashboardOption();
                    }
                    break;
                case "0":
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (!option.equals("0"));
    }

    private static String getMenu(LoginStats loginStats) {
        String menu = "\n";
        menu += "=======================================================\n";
        menu += String.format("Bank Secure - %s\n", loginStats.name);
        menu += "=======================================================\n";
        menu += "\n";
        menu += loginStats == LoginStats.ANONIMO ? "1. Login\n" : "1. Deslogar\n";
        menu += "2. Cadastro de Funcionário\n"; // Todo: Verificar a necessidade de adicionar
        menu += "3. Visualizar seguros disponíveis\n";

        if (loginStats == LoginStats.LOGADO){
            menu += "4. Gerênciar Clientes\n";
            menu += "5. Gerênciar Seguros\n";
            menu += "6. Gerênciar Apólices\n";
            menu += "7. Visualizar Dashboard\n";
        }

        menu += "0. Sair\n";
        menu += "\n";
        menu += "Digite o número da opção desejada: ";

        return menu;
    }

    @Getter
    private enum LoginStats {
        LOGADO("Logado"),
        ANONIMO("Anônimo");

        private final String name;

        LoginStats(String name){
            this.name = name;
        }

    }

    private static boolean loginValidator(LoginStats loginStats) {
        if (loginStats != LoginStats.LOGADO) {
            System.out.println("Opção inválida.");
            return false;
        }
        else  {
            return true;
        }
    }

    // Opção 1
    private static LoginStats loginOption(Scanner scanner, LoginStats loginStats){
        if (loginStats == LoginStats.LOGADO){
            System.out.println("Usuário deslogado.\n");
            return LoginStats.ANONIMO;
        }

        System.out.print("Digite seu usuário: ");
        String user = scanner.nextLine();

        // Todo: Poderia ser adicionada criptografia hash
        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        if (user.equals("admin") && senha.equals("1234")){
            System.out.println("Login efetuado com sucesso.\n");
            return LoginStats.LOGADO;
        }

        System.out.println("Usuário ou senha incorretos.\n");
        return LoginStats.ANONIMO;
    }

    // Opção 2
    private static void CadastroOption(){
        System.out.println("Not Implemented");
    }

    // Opção 3
    private static void SecureTypesViewOption(){
        // Todo: Puxar do banco de dados
        List<String> secureTypes = List.of(
                "Seguro de Vida",
                "Seguro de Automóvel",
                "Seguro Residencial",
                "Seguro Saúde",
                "Seguro Viagem"
        );

        System.out.println("Seguros disponíveis:\n");
        secureTypes.forEach(type -> System.out.println("- " + type));
        System.out.println();
    }

    // Opção 4
    private static void ManageClientsOption() {
        System.out.println("Not Implemented");
    }

    // Opção 5
    private static void ManageSecuresOption() {
        System.out.println("Not Implemented");
    }

    // Opção 6
    private static void ManageApoliciesOption() {
        System.out.println("Not Implemented");
    }

    // Opção 7
    private static void ViewDashboardOption() {
        System.out.println("Not Implemented");
    }
}

