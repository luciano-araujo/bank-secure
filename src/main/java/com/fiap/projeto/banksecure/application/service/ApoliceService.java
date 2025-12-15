package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Apolice;
import com.fiap.projeto.banksecure.domain.entity.Cliente;
import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.application.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.application.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.application.dto.DashboardDTO;
import com.fiap.projeto.banksecure.infra.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.infra.repository.ClienteRepository;
import com.fiap.projeto.banksecure.infra.repository.SeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ApoliceService {

    private final CotacaoService cotacaoService;
    private final ApoliceRepository apoliceRepository;
    private final ClienteRepository clienteRepository;
    private final SeguroRepository seguroRepository;

    public void validarCadastro(Apolice apolice) {
        if (apolice == null) {
            throw new IllegalArgumentException("Apolice é obrigatoria.");
        }

        if (apolice.getCliente() == null) {
            throw new IllegalArgumentException("A apolice deve estar atrelada a um cliente.");
        }

        if (apolice.getSeguro() == null) {
            throw new IllegalArgumentException("A apolice deve estar atrelada a um seguro.");
        }

        if (apolice.getPremioFinal() == null || apolice.getPremioFinal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Apolice deve ter valor final positivo.");
        }

        if (apolice.getDataInicial() == null) {
            throw new IllegalArgumentException("Apolice deve ter data de inicio.");
        }

        if (apolice.getDataVencimento() == null) {
            throw new IllegalArgumentException("Apolice deve ter data de vencimento.");
        }
    }

    public ApoliceDTO criarApolice(ApoliceDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));

        Seguro seguro = seguroRepository.findById(dto.seguroId())
                .orElseThrow(() -> new IllegalArgumentException("Seguro nao encontrado"));

        CotacaoDTO cotacaoCalculada = cotacaoService.calcularCotacao(dto.clienteId(), dto.seguroId());

        Apolice apolice = dto.toEntity();
        apolice.setCliente(cliente);
        apolice.setSeguro(seguro);
        apolice.setPremioFinal(cotacaoCalculada.premioFinal());
        apolice.setDataInicial(LocalDate.now());
        apolice.setDataVencimento(LocalDate.now().plusYears(1));

        cotacaoService.persistirCotacao(dto.clienteId(), dto.seguroId(), cotacaoCalculada.premioFinal());

        validarCadastro(apolice);
        Apolice salva = apoliceRepository.save(apolice);
        return ApoliceDTO.fromEntity(salva);
    }

    public List<ApoliceDTO> listarApolicesAVencer() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);

        return apoliceRepository.findByDataVencimentoBetween(hoje, limite)
                .stream()
                .map(ApoliceDTO::fromEntity)
                .toList();
    }

    public ApoliceDTO renovarApolice(UUID apoliceId) {
        Apolice apoliceExistente = apoliceRepository.findById(apoliceId)
                .orElseThrow(() -> new IllegalArgumentException("Apolice nao encontrada com o ID: " + apoliceId));

        if (apoliceExistente.getDataVencimento().isAfter(LocalDate.now().plusDays(30))) {
            throw new IllegalArgumentException("Apolice so pode ser renovada quando faltar 30 dias ou menos para o vencimento. Data de vencimento: " + apoliceExistente.getDataVencimento());
        }

        Apolice novaApolice = new Apolice();
        novaApolice.setCliente(apoliceExistente.getCliente());
        novaApolice.setSeguro(apoliceExistente.getSeguro());
        novaApolice.setTotalCobertura(apoliceExistente.getTotalCobertura());
        novaApolice.setDataInicial(LocalDate.now());
        novaApolice.setDataVencimento(LocalDate.now().plusYears(1));

        CotacaoDTO cotacao = cotacaoService.calcularCotacao(
                novaApolice.getCliente().getId(),
                novaApolice.getSeguro().getId());
        novaApolice.setPremioFinal(cotacao.premioFinal());

        cotacaoService.persistirCotacao(
                novaApolice.getCliente().getId(),
                novaApolice.getSeguro().getId(),
                cotacao.premioFinal());

        validarCadastro(novaApolice);
        Apolice apoliceRenovada = apoliceRepository.save(novaApolice);

        return ApoliceDTO.fromEntity(apoliceRenovada);
    }

    public List<DashboardDTO> getDashboard() {
        return apoliceRepository.findDashboardPorTipoSeguro();
    }

    public List<ApoliceDTO> listarTodasApolices() {
        return apoliceRepository.findAll()
                .stream()
                .map(ApoliceDTO::fromEntity)
                .toList();
    }

    public List<ApoliceDTO> listarApolicesVencidas() {
        LocalDate hoje = LocalDate.now();
        return apoliceRepository.findByDataVencimentoBefore(hoje)
                .stream()
                .map(ApoliceDTO::fromEntity)
                .toList();
    }


}
