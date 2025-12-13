package com.fiap.projeto.banksecure.config;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDefaultFuncionario();
    }

    private void seedDefaultFuncionario() {
        final String defaultEmail = "admin@banksecure.com";

        if (funcionarioRepository.findByEmail(defaultEmail).isPresent()) {
            return;
        }

        Funcionario admin = new Funcionario();
        admin.setNome("Administrador BankSecure");
        admin.setEmail(defaultEmail);
        admin.setCpf("000.111.222-33");
        admin.setTelefone("+5511999999999");
        admin.setDataNascimento(LocalDate.of(1990, 1, 1));
        admin.setSenha(passwordEncoder.encode("Admin@123"));

        funcionarioRepository.save(admin);
    }
}

