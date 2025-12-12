package com.fiap.projeto.banksecure.console.menus;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.dto.ClienteDTO;
import com.fiap.projeto.banksecure.service.ClienteService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@Component
public class MenuCliente {
    // Opção 4 -> Futuramente ClientController
    public void start(Scanner scanner, ClienteService clienteService) {
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
                    listarClientes(clienteService);
                    break;
                case "2":
                    cadastrarCliente(scanner, clienteService);
                    break;
                case "3":
                    editarCliente(scanner, clienteService);
                    break;
                case "4":
                    excluirCliente(scanner, clienteService);
                    break;
                case "0":
                    System.out.println("Voltando...\n");
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }

        } while (!option.equals("0"));
    }

    private void listarClientes(ClienteService clienteService) {
        List<ClienteDTO> clienteList = clienteService.getAllClientes();

        if (clienteList.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }

        clienteList.forEach(c -> {
            System.out.println("ID: " + c.id());
            System.out.println("Nome: " + c.nome());
            System.out.println("CPF: " + c.cpf());
            System.out.println("Data de Nascimento: " + c.dataNascimento());
            System.out.println("---------------------------");
        });
    }

    private void cadastrarCliente(Scanner scanner, ClienteService clienteService) {
        try {
            System.out.print("Nome completo: ");
            String nome = scanner.nextLine();

            System.out.print("CPF: ");
            String cpf = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Data de nascimento (DD/MM/YYYY): ");
            String dataNascimentoStr = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr, formatter);

            System.out.print("Senha: ");
            String senha = scanner.nextLine();


            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();

            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setEmail(email);
            cliente.setDataNascimento(dataNascimento);
            cliente.setSenha(senha);
            cliente.setTelefone(telefone);

            clienteService.cadastrarCliente(ClienteDTO.fromEntity(cliente));

            System.out.println("Cliente Cadastrado com sucesso!\n");
        } catch (DateTimeParseException e) {
            System.out.println("Erro ao cadastrar cliente: Formato de data inválida");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage() + "\n");
        }
    }

    private void editarCliente(Scanner scanner, ClienteService clienteService) {
        try {
            System.out.print("CPF do cliente a editar: ");
            String cpf = scanner.nextLine();

            Cliente clienteExistente = clienteService.buscarPorCpf(cpf);

            System.out.print("Novo nome completo: ");
            String nome = scanner.nextLine();

            System.out.print("Novo CPF: ");
            String novoCpf = scanner.nextLine();

            System.out.print("Novo email: ");
            String email = scanner.nextLine();

            System.out.print("Nova data de nascimento (DD/MM/YYYY): ");
            String dataNascimentoStr = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr, formatter);

            System.out.print("Novo telefone: ");
            String telefone = scanner.nextLine();

            System.out.println(clienteExistente.getCpf());
            System.out.println(novoCpf);
            if (novoCpf.equals(clienteExistente.getCpf())) {
                System.out.println("Igual");
            }

            clienteExistente.setNome(nome);
            clienteExistente.setCpf(novoCpf);
            clienteExistente.setEmail(email);
            clienteExistente.setDataNascimento(dataNascimento);
            clienteExistente.setTelefone(telefone);

            clienteService.atualizarCliente(clienteExistente.getId(), ClienteDTO.fromEntity(clienteExistente));

            System.out.println("Cliente editado com sucesso!\n");

        } catch (DateTimeParseException e) {
            System.out.println("Erro ao editar cliente: Formato de data inválida");
        } catch (Exception e) {
            System.out.println("Erro ao editar cliente: " + e.getMessage() + "\n");
        }
    }

    private void excluirCliente(Scanner scanner, ClienteService clienteService) {
        try {
            System.out.print("CPF do cliente a excluir: ");
            String cpf = scanner.nextLine();

            Cliente cliente = clienteService.buscarPorCpf(cpf);

            clienteService.deletarCliente(cliente.getId());

            System.out.println("Cliente excluído com sucesso!\n");

        } catch (Exception e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage() + "\n");
        }
    }
}
