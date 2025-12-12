package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Cotacao;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.CotacaoRepository;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CotacaoService {

    private final CotacaoRepository cotacaoRepository;
    private final ClienteRepository clienteRepository;
    private final SeguroRepository seguroRepository;

    private static final BigDecimal TAXA_PADRAO = new BigDecimal("0.05");
    private static final BigDecimal BONUS_IDADE = new BigDecimal("100.00");
    private static final BigDecimal FATOR_RISCO = new BigDecimal("1.10");
    private static final int IDADE_BONUS = 60;


    public CotacaoDTO realizarCotacao(UUID clienteId, UUID seguroId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Seguro seguro = seguroRepository.findById(seguroId)
                .orElseThrow(() -> new IllegalArgumentException("Seguro não encontrado"));

        BigDecimal premioFinal = calcularPremioFinal(seguro.getValorPremioBase(), cliente);

        Cotacao cotacao = new Cotacao();
        cotacao.setCliente(cliente);
        cotacao.setSeguro(seguro);
        cotacao.setPremioBase(seguro.getValorPremioBase());
        cotacao.setPremioFinal(premioFinal);
        cotacao.setDataCalculo(LocalDate.now());

        Cotacao salva = cotacaoRepository.save(cotacao);
        return CotacaoDTO.fromEntity(salva);
    }


    private BigDecimal calcularPremioFinal(BigDecimal premioBase, Cliente cliente) {
        BigDecimal premio = premioBase;
        premio = premio.add(premioBase.multiply(TAXA_PADRAO));

        if (calcularIdade(cliente.getDataNascimento()) > IDADE_BONUS) {
            premio = premio.add(BONUS_IDADE);
        }
        premio = premio.multiply(FATOR_RISCO);

        return premio;
    }

    private int calcularIdade(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public CotacaoDTO buscarPorId(UUID id) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));
        return CotacaoDTO.fromEntity(cotacao);
    }

    public List<CotacaoDTO> getAllCotacoes() {
        return cotacaoRepository.findAll().stream()
                .map(CotacaoDTO::fromEntity)
                .toList();
    }
}
