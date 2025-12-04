package com.fiap.projetoFinal.banksecure;

import com.fiap.projetoFinal.banksecure.domain.Apolice;
import com.fiap.projetoFinal.banksecure.domain.Bem;
import com.fiap.projetoFinal.banksecure.domain.Cliente;
import com.fiap.projetoFinal.banksecure.domain.Funcionario;
import com.fiap.projetoFinal.banksecure.enums.TipoSeguroEnum;
import com.fiap.projetoFinal.banksecure.service.ApoliceService;
import com.fiap.projetoFinal.banksecure.service.ClienteService;
import com.fiap.projetoFinal.banksecure.service.CotacaoService;
import com.fiap.projetoFinal.banksecure.service.FuncionarioService;
import com.fiap.projetoFinal.banksecure.service.SeguroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final SeguroService seguroService;
    private final CotacaoService cotacaoService;
    private final ApoliceService apoliceService;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final List<Apolice> apolices = new ArrayList<>();

    public ConsoleRunner(ClienteService clienteService,
                         FuncionarioService funcionarioService,
                         SeguroService seguroService,
                         CotacaoService cotacaoService,
                         ApoliceService apoliceService) {
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
        this.seguroService = seguroService;
        this.cotacaoService = cotacaoService;
        this.apoliceService = apoliceService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        String opcao;
        do {
            mostrarMenu();
            opcao = scanner.nextLine().trim();
            System.out.println();
            switch (opcao) {
                case "1" -> cadastrarCliente(scanner);
                case "2" -> listarClientes();
                case "3" -> cadastrarFuncionario(scanner);
                case "4" -> listarFuncionarios();
                case "5" -> listarTiposSeguro();
                case "6" -> criarApolice(scanner);
                case "7" -> listarApolices();
                case "8" -> renovarApolice(scanner);
                case "9" -> mostrarDashboard();
                case "0" -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.\n");
            }
        } while (!"0".equals(opcao));
    }

    private void mostrarMenu() {
        System.out.println("=======================================");
        System.out.println("           Console BankSecure          ");
        System.out.println("=======================================");
        System.out.println("1. Cadastrar Cliente");
        System.out.println("2. Listar Clientes");
        System.out.println("3. Cadastrar Funcionário");
        System.out.println("4. Listar Funcionários");
        System.out.println("5. Listar tipos de seguro");
        System.out.println("6. Criar Apólice");
        System.out.println("7. Listar Apólices");
        System.out.println("8. Renovar Apólice");
        System.out.println("9. Dashboard por tipo de seguro");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void cadastrarCliente(Scanner scanner) {
        Cliente c = new Cliente();
        c.setId(UUID.randomUUID());
        System.out.print("Nome: ");
        c.setNome(scanner.nextLine());
        System.out.print("CPF: ");
        c.setCpf(scanner.nextLine());
        System.out.print("E-mail: ");
        c.setEmail(scanner.nextLine());
        System.out.print("Senha: ");
        c.setSenha(scanner.nextLine());
        System.out.print("Telefone: ");
        c.setTelefone(scanner.nextLine());
        try {
            clienteService.validarCadastro(c);
            clientes.add(c);
            System.out.println("Cliente cadastrado com sucesso.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage() + "\n");
        }
    }

    private void listarClientes() {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.\n");
            return;
        }
        System.out.println("Clientes cadastrados:");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            System.out.printf("%d) %s | CPF: %s | Email: %s | Tel: %s%n",
                    i + 1, c.getNome(), c.getCpf(), c.getEmail(), c.getTelefone());
        }
        System.out.println();
    }

    private void cadastrarFuncionario(Scanner scanner) {
        Funcionario f = new Funcionario();
        f.setId(UUID.randomUUID());
        System.out.print("Nome: ");
        f.setNome(scanner.nextLine());
        System.out.print("CPF: ");
        f.setCpf(scanner.nextLine());
        System.out.print("E-mail: ");
        f.setEmail(scanner.nextLine());
        System.out.print("Senha: ");
        f.setSenha(scanner.nextLine());
        System.out.print("Telefone: ");
        f.setTelefone(scanner.nextLine());
        try {
            funcionarioService.validarCadastro(f);
            funcionarios.add(f);
            System.out.println("Funcionário cadastrado com sucesso.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage() + "\n");
        }
    }

    private void listarFuncionarios() {
        if (funcionarios.isEmpty()) {
            System.out.println("Nenhum funcionário cadastrado.\n");
            return;
        }
        System.out.println("Funcionários cadastrados:");
        for (int i = 0; i < funcionarios.size(); i++) {
            Funcionario f = funcionarios.get(i);
            System.out.printf("%d) %s | CPF: %s | Email: %s | Tel: %s%n",
                    i + 1, f.getNome(), f.getCpf(), f.getEmail(), f.getTelefone());
        }
        System.out.println();
    }

    private void listarTiposSeguro() {
        var tipos = seguroService.listarTiposDisponiveis();
        System.out.println("Tipos de seguro disponíveis:");
        for (TipoSeguroEnum tipo : tipos) {
            System.out.println("- " + tipo.name());
        }
        System.out.println();
    }

    private void criarApolice(Scanner scanner) {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado. Cadastre um cliente antes.\n");
            return;
        }

        System.out.println("Selecione um cliente pelo número:");
        listarClientes();
        System.out.print("Número do cliente: ");
        String numCliStr = scanner.nextLine();
        int idxCliente;
        try {
            idxCliente = Integer.parseInt(numCliStr) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido.\n");
            return;
        }
        if (idxCliente < 0 || idxCliente >= clientes.size()) {
            System.out.println("Cliente inexistente.\n");
            return;
        }
        Cliente cliente = clientes.get(idxCliente);

        var tipos = TipoSeguroEnum.values();
        System.out.println("Tipos de seguro:");
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("%d) %s%n", i + 1, tipos[i].name());
        }
        System.out.print("Número do tipo: ");
        String tipoStr = scanner.nextLine();
        int idxTipo;
        try {
            idxTipo = Integer.parseInt(tipoStr) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido.\n");
            return;
        }
        if (idxTipo < 0 || idxTipo >= tipos.length) {
            System.out.println("Tipo inexistente.\n");
            return;
        }
        TipoSeguroEnum tipoEscolhido = tipos[idxTipo];

        List<Bem> bens = new ArrayList<>();
        while (true) {
            System.out.print("Adicionar bem? (s/n): ");
            String resp = scanner.nextLine().trim();
            if (!resp.equalsIgnoreCase("s")) break;

            Bem b = new Bem();
            b.setId(UUID.randomUUID());
            System.out.print("Descrição do bem: ");
            b.setDescricao(scanner.nextLine());
            System.out.print("Valor do bem: ");
            try {
                BigDecimal valor = new BigDecimal(scanner.nextLine());
                b.setValor(valor);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, bem ignorado.");
                continue;
            }
            bens.add(b);
        }

        try {
            Apolice ap = apoliceService.criarApolice(cliente, tipoEscolhido, bens);
            apolices.add(ap);
            System.out.println("Apólice criada com sucesso.");
            System.out.println("Total de cobertura: " + ap.getTotalCobertura());
            BigDecimal premio = cotacaoService.calcularPremio(ap.getTotalCobertura(), ap.getTipoSeguroEnum());
            System.out.println("Prêmio estimado: " + premio + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar apólice: " + e.getMessage() + "\n");
        }
    }

    private void listarApolices() {
        if (apolices.isEmpty()) {
            System.out.println("Nenhuma apólice criada.\n");
            return;
        }
        System.out.println("Apólices cadastradas:");
        for (int i = 0; i < apolices.size(); i++) {
            Apolice a = apolices.get(i);
            System.out.printf("%d) Cliente: %s | Tipo: %s | Cobertura: %s | Vencimento: %s%n",
                    i + 1,
                    a.getCliente() != null ? a.getCliente().getNome() : "-",
                    a.getTipoSeguroEnum() != null ? a.getTipoSeguroEnum().name() : "-",
                    a.getTotalCobertura(),
                    a.getVencimento());
        }
        System.out.println();
    }

    private void renovarApolice(Scanner scanner) {
        if (apolices.isEmpty()) {
            System.out.println("Nenhuma apólice para renovar.\n");
            return;
        }
        listarApolices();
        System.out.print("Número da apólice para renovar: ");
        String numStr = scanner.nextLine();
        int idx;
        try {
            idx = Integer.parseInt(numStr) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido.\n");
            return;
        }
        if (idx < 0 || idx >= apolices.size()) {
            System.out.println("Apólice inexistente.\n");
            return;
        }
        Apolice atual = apolices.get(idx);
        try {
            Apolice renovada = apoliceService.renovarApolice(atual);
            apolices.add(renovada);
            System.out.println("Apólice renovada com sucesso.");
            System.out.println("Nova data de vencimento: " + renovada.getVencimento());
            System.out.println("Total de cobertura: " + renovada.getTotalCobertura());
            BigDecimal premio = cotacaoService.calcularPremio(renovada.getTotalCobertura(), renovada.getTipoSeguroEnum());
            System.out.println("Novo prêmio estimado: " + premio + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao renovar apólice: " + e.getMessage() + "\n");
        }
    }

    private void mostrarDashboard() {
        if (apolices.isEmpty()) {
            System.out.println("Nenhuma apólice cadastrada para montar dashboard.\n");
            return;
        }
        Map<TipoSeguroEnum, ApoliceService.ResumoDashboard> mapa =
                apoliceService.dashboardPorTipo(apolices);
        System.out.println("Dashboard por tipo de seguro:");
        for (Map.Entry<TipoSeguroEnum, ApoliceService.ResumoDashboard> entry : mapa.entrySet()) {
            TipoSeguroEnum tipo = entry.getKey();
            ApoliceService.ResumoDashboard r = entry.getValue();
            System.out.printf("Tipo: %s | Quantidade: %d | Cobertura total: %s%n",
                    tipo.name(), r.quantidade, r.totalCobertura);
        }
        System.out.println();
    }
}
