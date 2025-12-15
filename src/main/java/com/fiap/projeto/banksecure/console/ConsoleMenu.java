package com.fiap.projeto.banksecure.console;

import com.fiap.projeto.banksecure.console.menus.MenuApolices;
import com.fiap.projeto.banksecure.console.menus.MenuCliente;
import com.fiap.projeto.banksecure.console.menus.MenuSeguro;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.dto.ClienteDTO;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.service.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleMenu {
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final SeguroService seguroService;
    private final CotacaoService cotacaoService;
    private final ApoliceService apoliceService;
    private final AuthService authService;

    private final MenuCliente menuCliente;
    private final MenuApolices menuApolices;
    private final MenuSeguro menuSeguro;

    public void start(){
        boolean fazerCadastroInicial = true;
        if(fazerCadastroInicial){
            criarUsuariosIniciais();
        }

        String option;
        Scanner scanner = new Scanner(System.in);
        LoginStats loginStats = LoginStats.ANONIMO;

        do {
            System.out.print(getMenu(loginStats));
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
                    CotacaoOption(scanner);
                    break;
                case "4":
                    SecureTypesViewOption();
                    break;
                case "5":
                    if (loginValidator(loginStats)) {
                        menuCliente.start(scanner, clienteService);
                    }
                    break;
                case "6":
                    if (loginValidator(loginStats)) {
                        menuSeguro.start(scanner, seguroService);
                    }
                    break;
                case "7":
                    if (loginValidator(loginStats)) {
                        menuApolices.start(scanner, apoliceService, clienteService, seguroService, cotacaoService);
                    }
                    break;
                case "8":
                    if (loginValidator(loginStats)) {
                        ViewDashboardOption();
                    }
                    break;
                case "0":
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        } while (!option.equals("0"));
    }

    private void criarUsuariosIniciais() {
        Funcionario funcionario = new Funcionario();
        funcionario.setCpf("123456789");
        funcionario.setEmail("email@email.com");
        funcionario.setNome("Funcionario Inicial");
        funcionario.setSenha("123");
        funcionario.setTelefone("11999999999");
        funcionario.setDataNascimento(LocalDate.of(1990, 1, 1));
        funcionarioService.cadastrarFuncionario(FuncionarioDTO.fromEntity(funcionario));

        Cliente cliente = new Cliente();
        cliente.setCpf("123");
        cliente.setEmail("cliente@mail.com");
        cliente.setNome("Cliente Inicial");
        cliente.setSenha("123");
        cliente.setTelefone("11988888888");
        cliente.setDataNascimento(LocalDate.of(1995, 5, 15));
        clienteService.cadastrarCliente(ClienteDTO.fromEntityFull(cliente));
    }

    private static String getMenu(LoginStats loginStats) {
        String menu = "\n";
        menu += "=======================================================\n";
        menu += String.format("Bank Secure - %s\n", loginStats.name);
        menu += "=======================================================\n";
        menu += "\n";
        menu += loginStats == LoginStats.ANONIMO ? "1. Login\n" : "1. Deslogar\n";
        menu += "2. Cadastro de Funcionario\n";
        menu += "3. Calcular Cotacao\n";
        menu += "4. Visualizar seguros disponiveis\n";

        if (loginStats == LoginStats.LOGADO){
            menu += "5. Gerenciar Clientes\n";
            menu += "6. Gerenciar Seguros\n";
            menu += "7. Gerenciar Apolices\n";
            menu += "8. Visualizar Dashboard\n";
        }

        menu += "0. Sair\n";
        menu += "\n";
        menu += "Digite o numero da opcao desejada: ";

        return menu;
    }

    @Getter
    private enum LoginStats {
        LOGADO("Logado"),
        ANONIMO("Anonimo");

        private final String name;

        LoginStats(String name){
            this.name = name;
        }
    }

    private boolean loginValidator(LoginStats loginStats) {
        if (loginStats != LoginStats.LOGADO) {
            System.out.println("Opcao invalida.");
            return false;
        }
        return true;
    }

    // Opcao 1
    private LoginStats loginOption(Scanner scanner, LoginStats loginStats){
        if (loginStats == LoginStats.LOGADO){
            System.out.println("Usuario deslogado.\n");
            return LoginStats.ANONIMO;
        }

        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        AuthRequest authRequest = new AuthRequest(email, senha);
        AuthResponse response;
        try {
            response = authService.login(authRequest);
        }catch (RuntimeException e){
            System.out.print("Email ou senha invalidos.");
            return LoginStats.ANONIMO;
        }

        if (response.authenticated())
            return LoginStats.LOGADO;

        System.out.println("Email ou senha incorretos.\n");
        return LoginStats.ANONIMO;
    }

    // Opcao 2
    private void CadastroOption(Scanner scanner){
        System.out.print("\nCadastro de Funcionario:\n\n");

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
            System.out.print("Funcionario cadastrado com sucesso.\n");
        } catch (IllegalArgumentException e) {
            System.out.printf("Campos preenchidos incorretamente. %s\n", e.getMessage());
        }
    }

    // Opcao 3
    private void CotacaoOption(Scanner scanner) {
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

        System.out.println("Clientes:");
        for (int i = 0; i < clientes.size(); i++) {
            var c = clientes.get(i);
            System.out.printf("%d) %s | CPF: %s%n", i + 1, c.nome(), c.cpf());
        }
        System.out.print("Escolha o numero do cliente: ");
        int idxCliente;
        try {
            idxCliente = Integer.parseInt(scanner.nextLine()) - 1;
            if (idxCliente < 0 || idxCliente >= clientes.size()) {
                System.out.println("Cliente inexistente.\n");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.\n");
            return;
        }
        var cliente = clientes.get(idxCliente);

        System.out.println("Seguros:");
        for (int i = 0; i < seguros.size(); i++) {
            var s = seguros.get(i);
            System.out.printf("%d) %s | Cobertura minima: %s | Premio base: %s%n", i + 1, s.titulo(), s.coberturaMinima(), s.valorPremioBase());
        }
        System.out.print("Escolha o numero do seguro: ");
        int idxSeguro;
        try {
            idxSeguro = Integer.parseInt(scanner.nextLine()) - 1;
            if (idxSeguro < 0 || idxSeguro >= seguros.size()) {
                System.out.println("Seguro inexistente.\n");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.\n");
            return;
        }
        var seguro = seguros.get(idxSeguro);

        try {
            var cotacao = cotacaoService.calcularCotacao(cliente.id(), seguro.id());
            System.out.println("Cotacao realizada:");
            System.out.printf("Premio base: %s%n", cotacao.premioBase());
            System.out.printf("Premio final: %s%n", cotacao.premioFinal());
            System.out.printf("Data do calculo: %s%n%n", cotacao.dataCalculo());
        } catch (Exception e) {
            System.out.println("Erro ao calcular cotacao: " + e.getMessage() + "\n");
        }
    }

    // Opcao 4
    private void SecureTypesViewOption(){
        var seguros = seguroService.getAllSeguros();
        if (seguros.isEmpty()) {
            System.out.println("Nenhum seguro cadastrado.\n");
            return;
        }

        System.out.println("Seguros disponiveis:\n");
        seguros.forEach(s -> System.out.printf("- %s | Cobertura minima: %s | Premio base: %s%n",
                s.titulo(), s.coberturaMinima(), s.valorPremioBase()));
        System.out.println();
    }

    // Opcao 8
    private void ViewDashboardOption() {
        var dashboard = apoliceService.getDashboard();
        if (dashboard == null || dashboard.isEmpty()) {
            System.out.println("Nenhuma apolice registrada para montar dashboard.\n");
            return;
        }

        System.out.println("Dashboard por tipo de seguro:");
        dashboard.forEach(d -> System.out.printf("Tipo: %s | Qtde apolices: %d | Valor total arrecadado: %s%n",
                d.getTipoSeguro(), d.getQuantidadeApolices(), d.getValorTotalArrecadado()));
        System.out.println();
    }
}
