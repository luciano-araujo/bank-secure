package com.fiap.projetoFinal.banksecure.service;

import com.fiap.projetoFinal.banksecure.domain.Apolice;
import com.fiap.projetoFinal.banksecure.domain.Bem;
import com.fiap.projetoFinal.banksecure.domain.Cliente;
import com.fiap.projetoFinal.banksecure.enums.TipoSeguroEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApoliceService {

    private final CotacaoService cotacaoService = new CotacaoService();

    public Apolice criarApolice(Cliente cliente,
                                TipoSeguroEnum tipoSeguro,
                                List<Bem> bens) {

        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para criar uma apólice.");
        }

        if (tipoSeguro == null) {
            throw new IllegalArgumentException("Tipo de seguro é obrigatório para criar uma apólice.");
        }

        Apolice apolice = new Apolice();
        apolice.setCliente(cliente);
        apolice.setTipoSeguroEnum(tipoSeguro);
        apolice.setVencimento(LocalDate.now().plusYears(1));

        if (bens != null) {
            for (Bem bem : bens) {
                if (bem != null) {
                    bem.setApolice(apolice);
                    apolice.getListaDeBens().add(bem);
                }
            }
        }

        BigDecimal totalCobertura = cotacaoService.calcularTotalCobertura(apolice.getListaDeBens());
        apolice.setTotalCobertura(totalCobertura);

        return apolice;
    }

    public Apolice renovarApolice(Apolice apoliceAtual) {
        if (apoliceAtual == null) {
            throw new IllegalArgumentException("Apólice atual não pode ser nula para renovação.");
        }

        Cliente cliente = apoliceAtual.getCliente();
        TipoSeguroEnum tipo = apoliceAtual.getTipoSeguroEnum();
        List<Bem> bensAtuais = apoliceAtual.getListaDeBens();

        List<Bem> bensCopiados = new ArrayList<>();
        if (bensAtuais != null) {
            for (Bem bem : bensAtuais) {
                if (bem != null) {
                    Bem copia = new Bem();
                    copia.setDescricao(bem.getDescricao());
                    copia.setValor(bem.getValor());
                    bensCopiados.add(copia);
                }
            }
        }

        return criarApolice(cliente, tipo, bensCopiados);
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