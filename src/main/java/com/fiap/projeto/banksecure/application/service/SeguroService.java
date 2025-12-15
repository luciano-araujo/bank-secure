package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Seguro;
import com.fiap.projeto.banksecure.application.dto.SeguroDTO;
import com.fiap.projeto.banksecure.infra.repository.SeguroRepository;
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
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado com o ID: " + id));

        seguroExistente.setTitulo(seguroDTO.titulo());
        seguroExistente.setCoberturaMinima(seguroDTO.coberturaMinima());
        seguroExistente.setValorPremioBase(seguroDTO.valorPremioBase());

        validarSeguro(seguroExistente);
        Seguro atualizado = seguroRepository.save(seguroExistente);
        return SeguroDTO.fromEntity(atualizado);
    }

    public void deletarSeguro(UUID id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado"));
        seguroRepository.delete(seguro);
    }

    public SeguroDTO buscarPorId(UUID id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado"));
        return SeguroDTO.fromEntity(seguro);
    }

    public List<SeguroDTO> getAllSeguros() {
        return seguroRepository.findAll().stream()
                .map(SeguroDTO::fromEntity)
                .toList();
    }

    protected void validarSeguro(Seguro seguro) {
        if (seguro.getTitulo() == null || seguro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        if (seguro.getValorPremioBase() == null) {
            throw new IllegalArgumentException("Valor de Prêmio Base é obrigatório.");
        }

        if (BigDecimal.ZERO.compareTo(seguro.getValorPremioBase()) >= 0) {
            throw new IllegalArgumentException("Valor de Prêmio Base deve ser positivo.");
        }
    }
}
