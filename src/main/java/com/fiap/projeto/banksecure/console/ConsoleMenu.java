package com.fiap.projeto.banksecure.console;

import com.fiap.projeto.banksecure.console.menus.MenuApolices;
import com.fiap.projeto.banksecure.console.menus.MenuCliente;
import com.fiap.projeto.banksecure.console.menus.MenuSeguro;
import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.service.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleMenu {
    @Value("${skip.console:false}")
    private boolean skipConsole;

    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final SeguroService seguroService;
    private final CotacaoService cotacaoService;
    private final ApoliceService apoliceService;

    private final MenuCliente menuCliente;
    private final MenuApolices menuApolices;
    private final MenuSeguro menuSeguro;

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
                    CadastroOption(scanner);
                    break;
                case "3":
                    SecureTypesViewOption();
                    break;
                case "4":
                    if (loginValidator(loginStats)) {
                        menuCliente.start(scanner);
                    }
                    break;
                case "5":
                    if (loginValidator(loginStats)) {
                        menuSeguro.start(scanner);
                    }
                    break;
                case "6":
                    if (loginValidator(loginStats)) {
                        menuApolices.start(scanner);
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

    private boolean loginValidator(LoginStats loginStats) {
        if (loginStats != LoginStats.LOGADO) {
            System.out.println("Opção inválida.");
            return false;
        }
        else  {
            return true;
        }
    }

    // Opção 1
    private LoginStats loginOption(Scanner scanner, LoginStats loginStats){
        if (loginStats == LoginStats.LOGADO){
            System.out.println("Usuário deslogado.\n");
            return LoginStats.ANONIMO;
        }

        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        AuthRequest authRequest = new AuthRequest(email, senha);
        AuthResponse response;
        try {
            response = funcionarioService.logar(authRequest);
        }catch (RuntimeException e){
            System.out.print("Email ou senha inválidos.");
            return LoginStats.ANONIMO;
        }

        if (response.authenticated())
            return LoginStats.LOGADO;

        System.out.println("Email ou senha incorretos.\n");
        return LoginStats.ANONIMO;
    }

    // Opção 2
    private void CadastroOption(Scanner scanner){
        System.out.print("\nCadastro de Funcionário:\n\n");

        System.out.print("Digite seu Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Digite seu CPF: ");
        String cpf = scanner.nextLine();

        System.out.print("Digite seu E-mail: ");
        String email = scanner.nextLine();

        System.out.print("Digite seu telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        FuncionarioDTO novoCadastro = new FuncionarioDTO(null,nome, cpf, email, senha, telefone, null);
        try {
            funcionarioService.cadastrarFuncionario(novoCadastro);

            System.out.print("Funcionário cadastrado com sucesso.\n");
        } catch (IllegalArgumentException e) {
            System.out.printf("Campos preenchidos incorretamente. %s\n", e.getMessage());
        }
    }

    // Opção 3 -> Futuramente Dentro de SecureController
    private void SecureTypesViewOption(){
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
    private void ViewDashboardOption() {
        System.out.println("Not Implemented");
    }
}

