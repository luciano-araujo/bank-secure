package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Seguro;
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

    public SeguroDTO cadastrarSeguro(SeguroDTO dto) {
        Seguro seguro = dto.toEntity();
        validarSeguro(seguro);
        Seguro saved = seguroRepository.save(seguro);
        return SeguroDTO.fromEntity(saved);
    }

    public SeguroDTO atualizarSeguro(UUID id, SeguroDTO seguroDTO) {
        Seguro seguroExistente = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro nÆo encontrado com o ID: " + id));

        seguroExistente.setTitulo(seguroDTO.titulo());
        seguroExistente.setTipo(seguroDTO.tipo());
        seguroExistente.setCoberturaMinima(seguroDTO.coberturaMinima());
        seguroExistente.setValorPremioBase(seguroDTO.valorPremioBase());

        validarSeguro(seguroExistente);
        Seguro atualizado = seguroRepository.save(seguroExistente);
        return SeguroDTO.fromEntity(atualizado);
    }

    public void deletarSeguro(UUID id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro nÆo encontrado"));
        seguroRepository.delete(seguro);
    }

    public SeguroDTO buscarPorId(UUID id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro nÆo encontrado"));
        return SeguroDTO.fromEntity(seguro);
    }

    public List<SeguroDTO> getAllSeguros() {
        return seguroRepository.findAll().stream()
                .map(SeguroDTO::fromEntity)
                .toList();
    }


    protected void validarSeguro(Seguro seguro) {
        if (seguro.getTitulo() == null || seguro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("T¡tulo ‚ obrigat¢rio.");
        }

        if (seguro.getTipo() == null) {
            throw new IllegalArgumentException("Tipo de seguro ‚ obrigat¢rio.");
        }

        if (seguro.getCoberturaMinima() == null) {
            throw new IllegalArgumentException("Cobertura m¡nima ‚ obrigat¢ria.");
        }

        if (BigDecimal.ZERO.compareTo(seguro.getCoberturaMinima()) >= 0) {
            throw new IllegalArgumentException("Cobertura m¡nima deve ser positiva.");
        }

        if (seguro.getValorPremioBase() == null) {
            throw new IllegalArgumentException("Valor de Prˆmio Base ‚ obrigat¢rio.");
        }

        if (BigDecimal.ZERO.compareTo(seguro.getValorPremioBase()) >= 0) {
            throw new IllegalArgumentException("Valor de Prˆmio Base deve ser positivo.");
        }

        var existente = seguroRepository.findByTitulo(seguro.getTitulo());
        if (existente.isPresent() && (seguro.getId() == null || !existente.get().getId().equals(seguro.getId()))) {
            throw new IllegalArgumentException("Já existe um seguro com este título.");
        }
    }
}
