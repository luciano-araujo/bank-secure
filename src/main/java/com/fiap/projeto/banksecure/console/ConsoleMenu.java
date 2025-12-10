package com.fiap.projeto.banksecure.console;

import com.fiap.projeto.banksecure.console.menus.MenuApolices;
import com.fiap.projeto.banksecure.console.menus.MenuCliente;
import com.fiap.projeto.banksecure.console.menus.MenuSeguro;
import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.service.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleMenu {
    @Value("${skip.console:false}")
    private boolean skipConsole;

    @Autowired
    private final ClienteService clienteService = new ClienteService();
    @Autowired
    private final FuncionarioService funcionarioService = new FuncionarioService();
    @Autowired
    private final SeguroService seguroService = new SeguroService();
    @Autowired
    private final CotacaoService cotacaoService = new CotacaoService();
    @Autowired
    private final ApoliceService apoliceService = new ApoliceService();

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final List<Apolice> apolices = new ArrayList<>();

    public void start(){
        if (this.skipConsole){
            return;
        }

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
                        MenuCliente.start(scanner);
                    }
                    break;
                case "5":
                    if (loginValidator(loginStats)) {
                        MenuSeguro.start(scanner);
                    }
                    break;
                case "6":
                    if (loginValidator(loginStats)) {
                        MenuApolices.start(scanner);
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

    // Opção 3 -> Futuramente Dentro de SecureController
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

    // Opção 7
    private static void ViewDashboardOption() {
        System.out.println("Not Implemented");
    }
}

