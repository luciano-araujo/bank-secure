package com.fiap.projeto.banksecure.config;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final FuncionarioRepository funcionarioRepository;
    private final SeguroRepository seguroRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDefaultFuncionario();
        seedDefaultSeguros();
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

    private void seedDefaultSeguros() {
        if (seguroRepository.count() > 0) {
            return;
        }

        List<Seguro> defaults = List.of(
                criarSeguro("Seguro Residencial Essencial", TipoSeguroEnum.RESIDENCIAL, new BigDecimal("150000"), new BigDecimal("450")),
                criarSeguro("Seguro Auto Completo", TipoSeguroEnum.AUTOMOTIVO, new BigDecimal("60000"), new BigDecimal("650")),
                criarSeguro("Seguro Vida Familiar", TipoSeguroEnum.VIDA, new BigDecimal("80000"), new BigDecimal("350"))
        );

        defaults.forEach(seguroRepository::save);
    }

    private Seguro criarSeguro(String titulo, TipoSeguroEnum tipo, BigDecimal cobertura, BigDecimal premioBase) {
        Seguro seguro = new Seguro();
        seguro.setTitulo(titulo);
        seguro.setTipo(tipo);
        seguro.setCoberturaMinima(cobertura);
        seguro.setValorPremioBase(premioBase);
        return seguro;
    }
}
