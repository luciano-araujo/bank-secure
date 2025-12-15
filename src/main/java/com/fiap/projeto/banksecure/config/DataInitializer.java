package com.fiap.projeto.banksecure.config;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // cria admin padrao se nao existir nenhum funcionario
            if (funcionarioRepository.count() == 0) {
                Funcionario admin = new Funcionario();
                admin.setNome("Administrador");
                admin.setCpf("11111111111");
                admin.setEmail("admin@banksecure.com");
                admin.setDataNascimento(LocalDate.of(1990, 1, 1));
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setTelefone("11999999999");

                funcionarioRepository.save(admin);

                System.out.println("========================================");
                System.out.println("FUNCIONARIO PADRAO CRIADO:");
                System.out.println("CPF: 11111111111");
                System.out.println("Senha: admin123");
                System.out.println("========================================");
            }
        };
    }
}
