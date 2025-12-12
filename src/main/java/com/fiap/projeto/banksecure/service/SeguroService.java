package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.dto.SeguroDTO;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SeguroService {

    private final SeguroRepository seguroRepository;

    public void validarCadastro(Seguro seguro) {
        if (seguro == null) {
            throw new IllegalArgumentException("Seguro é obrigatório.");
        }

        if (seguro.getTitulo() == null || seguro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título do seguro é obrigatório.");
        }

        if (seguro.getTipoSeguroEnum() == null || seguro.getTipoSeguroEnum().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo do seguro é obrigatório.");
        }

        if ((seguro.getValorPremioBase() == null) || (BigDecimal.ZERO.compareTo(seguro.getValorPremioBase()) <= 0)) {
            throw new IllegalArgumentException("Prêmio base deve ser maior que zero.");
        }

        if (seguro.getDescricao() == null || seguro.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do seguro é obrigatória.");
        }

        if (seguro.getCoberturaMinima() == null || seguro.getCoberturaMinima().trim().isEmpty()) {
            throw new IllegalArgumentException("Cobertura Mínima do seguro é obrigatória.");
        }
    }

    public SeguroDTO cadastrarSeguro(SeguroDTO seguroDTO) {
        Seguro seguro = seguroDTO.toEntity();

        validarCadastro(seguro);
        Seguro seguroCadastrado = seguroRepository.save(seguro);

        return SeguroDTO.fromEntity(seguroCadastrado);
    }

    public SeguroDTO atualizarSeguro(UUID id, SeguroDTO seguroDTO) throws IllegalArgumentException {
        Seguro seguroExistente = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado com o ID: " + id));

        List<Apolice> apolices = seguroDTO.apolices().stream()
                .map(ApoliceDTO::toEntity)
                .toList();

        seguroExistente.setTitulo(seguroDTO.titulo());
        seguroExistente.setValorPremioBase(seguroDTO.valorPremioBase());
        seguroExistente.setDescricao(seguroDTO.descricao());
        seguroExistente.setCoberturaMinima(seguroDTO.coberturaMinima());
        seguroExistente.setTipoSeguroEnum(seguroDTO.tipoSeguroEnum());
        seguroExistente.setApolices(apolices);

        validarCadastro(seguroExistente);
        Seguro seguroAtualizado = seguroRepository.save(seguroExistente);

        return SeguroDTO.fromEntity(seguroAtualizado);
    }

    public void deletarSeguro(UUID id) throws RuntimeException {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado"));

        seguroRepository.delete(seguro);
    }

    public SeguroDTO buscarPorId(UUID id) throws RuntimeException {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado"));

        return SeguroDTO.fromEntity(seguro);
    }

    public List<SeguroDTO> getAllSeguros() {
        return seguroRepository
                .findAll()
                .stream()
                .map(SeguroDTO::fromEntity)
                .toList();
    }
}