package com.fiap.projeto.banksecure.config;

import com.fiap.projeto.banksecure.domain.entity.Apolice;
import com.fiap.projeto.banksecure.domain.entity.Cliente;
import com.fiap.projeto.banksecure.domain.entity.Funcionario;
import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.infra.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.infra.repository.ClienteRepository;
import com.fiap.projeto.banksecure.infra.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.infra.repository.SeguroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(FuncionarioRepository funcionarioRepository,
                                   SeguroRepository seguroRepository,
                                   ApoliceRepository apoliceRepository,
                                   ClienteRepository clienteRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (funcionarioRepository.count() == 0) {
                Funcionario admin = new Funcionario();
                admin.setNome("Administrador");
                admin.setCpf("434.132.190-00");
                admin.setEmail("admin@banksecure.com");
                admin.setDataNascimento(LocalDate.of(1990, 1, 1));
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setTelefone("(11)94002-8922");

                funcionarioRepository.save(admin);

                System.out.println("========================================");
                System.out.println("FUNCIONARIO PADRAO CRIADO:");
                System.out.println("CPF: 11111111111");
                System.out.println("Senha: admin123");
                System.out.println("========================================");
            }

            if (seguroRepository.count() == 0) {
                // Seguro Auto
                Seguro seguroAuto = new Seguro();
                seguroAuto.setTitulo("Seguro Auto");
                seguroAuto.setCoberturaMinima(("30000.00"));
                seguroAuto.setValorPremioBase(new BigDecimal("1800.00"));

                // Seguro Residencial
                Seguro seguroResidencial = new Seguro();
                seguroResidencial.setTitulo("Seguro Residencial");
                seguroResidencial.setCoberturaMinima(("150000.00"));
                seguroResidencial.setValorPremioBase(new BigDecimal("850.00"));

                // Seguro Vida
                Seguro seguroVida = new Seguro();
                seguroVida.setTitulo("Seguro Vida");
                seguroVida.setCoberturaMinima(("100000.00"));
                seguroVida.setValorPremioBase(new BigDecimal("600.00"));

                seguroRepository.save(seguroAuto);
                seguroRepository.save(seguroResidencial);
                seguroRepository.save(seguroVida);

                System.out.println("========================================");
                System.out.println("SEGUROS PADRAO CRIADOS:");
                System.out.println("- Auto: Cobertura R$30.000 | Premio R$1.800/ano");
                System.out.println("- Residencial: Cobertura R$150.000 | Premio R$850/ano");
                System.out.println("- Vida: Cobertura R$100.000 | Premio R$600/ano");
                System.out.println("========================================");
            }

            if (clienteRepository.count() == 0) {
                Cliente cliente = new Cliente();
                cliente.setNome("João Silva");
                cliente.setCpf("123.456.789-00");
                cliente.setEmail("joao@banksecure.com");
                cliente.setSenha("joaozinho123");
                cliente.setDataNascimento(LocalDate.of(1985, 5, 15));
                cliente.setTelefone("(11)98765-4321");
                cliente = clienteRepository.save(cliente);

                Seguro seguro = seguroRepository.findAll().get(0);

                Apolice apoliceVencida = new Apolice();
                apoliceVencida.setCliente(cliente);
                apoliceVencida.setSeguro(seguro);
                apoliceVencida.setTotalCobertura(new BigDecimal("50000.00"));
                apoliceVencida.setDataInicial(LocalDate.now().minusYears(2));
                apoliceVencida.setDataVencimento(LocalDate.now().minusMonths(1));
                apoliceVencida.setPremioFinal(new BigDecimal("2100.00"));
                apoliceRepository.save(apoliceVencida);

                System.out.println("========================================");
                System.out.println("APOLICE VENCIDA");
                System.out.println("========================================");

                Cliente maria = new Cliente();
                maria.setNome("Maria Souza");
                maria.setCpf("987.654.321-00");
                maria.setEmail("maria@banksecure.com");
                maria.setSenha("maria123");
                maria.setDataNascimento(LocalDate.of(1990, 8, 20));
                maria.setTelefone("(11)91234-5678");
                maria = clienteRepository.save(maria);

                Seguro seguroResidencial = seguroRepository.findAll().get(1);

                Apolice apoliceQuaseVencendo = new Apolice();
                apoliceQuaseVencendo.setCliente(maria);
                apoliceQuaseVencendo.setSeguro(seguroResidencial);
                apoliceQuaseVencendo.setTotalCobertura(new BigDecimal("200000.00"));
                apoliceQuaseVencendo.setDataInicial(LocalDate.now().minusYears(1));
                apoliceQuaseVencendo.setDataVencimento(LocalDate.now().plusDays(29));
                apoliceQuaseVencendo.setPremioFinal(new BigDecimal("950.00"));
                apoliceRepository.save(apoliceQuaseVencendo);

                System.out.println("========================================");
                System.out.println("APOLICE FALTANDO 29 DIAS PARA VENCER");
                System.out.println("Cliente: Maria Souza");
                System.out.println("Vencimento: " + apoliceQuaseVencendo.getDataVencimento());
                System.out.println("========================================");
            }
        };
    }
}
