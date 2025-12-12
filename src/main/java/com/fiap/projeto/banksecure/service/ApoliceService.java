package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Bem;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import com.fiap.projeto.banksecure.repository.ApoliceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ApoliceService {

    private final CotacaoService cotacaoService;
    private final ApoliceRepository apoliceRepository;

    public void validarCadastro(Apolice apolice) {
        if (apolice == null) {
            throw new IllegalArgumentException("Apolice é obrigatória.");
        }

        if (apolice.getCliente() == null) {
            throw new IllegalArgumentException("A apólice deve estar atrelada a um cliente.");
        }

        if (apolice.getTipoSeguroEnum() == null) {
            throw new IllegalArgumentException("Tipo do seguro é obrigatório.");
        }

        if ((apolice.getTotalCobertura() == null) || (BigDecimal.ZERO.compareTo(apolice.getTotalCobertura()) <= 0)) {
            throw new IllegalArgumentException("Apólice deve ter total de cobertura.");
        }

        if (apolice.getDataInicial() == null) {
            throw new IllegalArgumentException("Apólice deve ter data de início.");
        }

        if (apolice.getDataVencimento() == null) {
            throw new IllegalArgumentException("Apólice deve ter data de vencimento.");
        }

        if (apolice.getListaDeBens() == null) {
            throw new IllegalArgumentException("Apólice deve estar relacionada a uma lista de bens.");
        }

        if (apolice.getSeguro() == null) {
            throw new IllegalArgumentException("Apólice deve estar relacionada a um seguro.");
        }
    }

    public ApoliceDTO criarApolice(ApoliceDTO apoliceDTO) {
        Apolice apolice = apoliceDTO.toEntity();

        validarCadastro(apolice);
        Apolice apoliceCadastrada = apoliceRepository.save(apolice);

        return ApoliceDTO.fromEntity(apoliceCadastrada);
    }

    public ApoliceDTO renovarApolice(Apolice apolice) {

        Apolice apoliceExistente = apoliceRepository.findById(apolice.getId())
                .orElseThrow(() -> new IllegalArgumentException("Apólice não encontrada com o ID: " + apolice.getId()));

        LocalDate novaDataVencimento = apoliceExistente.getDataVencimento().plusYears(1);
        apoliceExistente.setDataVencimento(novaDataVencimento);

        validarCadastro(apoliceExistente);
        Apolice apoliceRenovada = apoliceRepository.save(apoliceExistente);

        return ApoliceDTO.fromEntity(apoliceRenovada);
    }

    public Map<TipoSeguroEnum, ResumoDashboard> dashboardPorTipo(List<Apolice> apolices) {

        Map<TipoSeguroEnum, ResumoDashboard> mapa = new HashMap<>();

        if (apolices == null || apolices.isEmpty()) {
            return mapa;
        }

        for (Apolice ap : apolices) {
            if (ap == null || ap.getTipoSeguroEnum() == null) {
                continue;
            }

            TipoSeguroEnum tipo = ap.getTipoSeguroEnum();

            mapa.putIfAbsent(tipo, new ResumoDashboard());
            ResumoDashboard resumo = mapa.get(tipo);

            resumo.quantidade++;

            if (ap.getTotalCobertura() != null) {
                resumo.totalCobertura = resumo.totalCobertura.add(ap.getTotalCobertura());
            }
        }

        return mapa;
    }

    public static class ResumoDashboard {
        public int quantidade = 0;
        public BigDecimal totalCobertura = BigDecimal.ZERO;
    }
}