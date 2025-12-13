package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Bem;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.dto.BemCadastroDTO;
import com.fiap.projeto.banksecure.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.dto.DashboardDTO;
import com.fiap.projeto.banksecure.dto.NovaApoliceDTO;
import com.fiap.projeto.banksecure.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApoliceService {

    private final CotacaoService cotacaoService;
    private final ApoliceRepository apoliceRepository;
    private final ClienteRepository clienteRepository;
    private final SeguroRepository seguroRepository;

    public void validarCadastro(Apolice apolice) {
        if (apolice == null) {
            throw new IllegalArgumentException("Apólice é obrigatória.");
        }

        if (apolice.getCliente() == null) {
            throw new IllegalArgumentException("A apólice deve estar atrelada a um cliente.");
        }

        if (apolice.getSeguro() == null) {
            throw new IllegalArgumentException("A apólice deve estar atrelada a um seguro.");
        }

        if (apolice.getPremioFinal() == null || apolice.getPremioFinal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Apólice deve ter valor final positivo.");
        }

        if (apolice.getDataInicial() == null) {
            throw new IllegalArgumentException("Apólice deve ter data de início.");
        }

        if (apolice.getDataVencimento() == null) {
            throw new IllegalArgumentException("Apólice deve ter data de vencimento.");
        }
    }

    public ApoliceDTO criarApolice(NovaApoliceDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Seguro seguro = seguroRepository.findById(dto.seguroId())
                .orElseThrow(() -> new IllegalArgumentException("Seguro não encontrado"));

        BigDecimal coberturaTotal = calcularCobertura(dto.bens());
        if (coberturaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("É obrigatório informar ao menos um bem com valor positivo.");
        }

        BigDecimal coberturaConsiderada = coberturaTotal.max(seguro.getCoberturaMinima());

        Apolice apolice = new Apolice();
        apolice.setCliente(cliente);
        apolice.setSeguro(seguro);
        apolice.setTipoSeguro(seguro.getTipo());
        apolice.setDataInicial(dto.dataInicial());
        apolice.setDataVencimento(dto.dataVencimento());
        apolice.setTotalCobertura(coberturaConsiderada);
        apolice.setListaDeBens(dto.bens().stream()
                .map(bemDTO -> toBem(bemDTO, apolice))
                .collect(Collectors.toCollection(ArrayList::new)));

        CotacaoDTO cotacao = cotacaoService.realizarCotacao(
                dto.clienteId(),
                dto.seguroId(),
                coberturaConsiderada
        );
        apolice.setPremioFinal(cotacao.premioFinal());

        validarCadastro(apolice);
        Apolice salva = apoliceRepository.save(apolice);
        return ApoliceDTO.fromEntity(salva);
    }

    private Bem toBem(BemCadastroDTO dto, Apolice apolice) {
        Bem bem = new Bem();
        bem.setTitulo(dto.titulo());
        bem.setValor(dto.valor());
        bem.setApolice(apolice);
        return bem;
    }

    private BigDecimal calcularCobertura(List<BemCadastroDTO> bens) {
        return bens.stream()
                .map(BemCadastroDTO::valor)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                .orElseThrow(() -> new IllegalArgumentException("Apólice não encontrada com o ID: " + apoliceId));

        Apolice novaApolice = new Apolice();
        novaApolice.setCliente(apoliceExistente.getCliente());
        novaApolice.setSeguro(apoliceExistente.getSeguro());
        novaApolice.setTipoSeguro(apoliceExistente.getSeguro().getTipo());
        novaApolice.setDataInicial(LocalDate.now());
        novaApolice.setDataVencimento(LocalDate.now().plusYears(1));

        List<Bem> bensRenovados = apoliceExistente.getListaDeBens().stream()
                .map(bemAntigo -> {
                    Bem bem = new Bem();
                    bem.setTitulo(bemAntigo.getTitulo());
                    bem.setValor(bemAntigo.getValor());
                    bem.setApolice(novaApolice);
                    return bem;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        novaApolice.setListaDeBens(bensRenovados);

        BigDecimal somaBens = bensRenovados.stream()
                .map(Bem::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (somaBens.compareTo(BigDecimal.ZERO) <= 0) {
            somaBens = apoliceExistente.getTotalCobertura();
        }

        BigDecimal cobertura = somaBens.max(apoliceExistente.getSeguro().getCoberturaMinima());

        novaApolice.setTotalCobertura(cobertura);

        CotacaoDTO cotacao = cotacaoService.realizarCotacao(
                novaApolice.getCliente().getId(),
                novaApolice.getSeguro().getId(),
                cobertura
        );
        novaApolice.setPremioFinal(cotacao.premioFinal());

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

}
