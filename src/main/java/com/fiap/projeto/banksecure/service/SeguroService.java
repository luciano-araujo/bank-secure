package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Seguro;
import com.fiap.projeto.banksecure.dto.SeguroRequest;
import com.fiap.projeto.banksecure.dto.SeguroResponse;
import com.fiap.projeto.banksecure.repository.SeguroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service

public class SeguroService {

    private final SeguroRepository seguroRepository;

    public SeguroService(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    public SeguroResponse cadastrar(SeguroRequest request) {
        validarSeguro(request.titulo(), request.valorPremioBase());

        Seguro seguro = new Seguro(
                request.titulo(),
                request.coberturaMinima(),
                request.valorPremioBase()
        );

        Seguro salvo = seguroRepository.save(seguro);
        return SeguroResponse.fromEntity(salvo);
    }

    public SeguroResponse alterar(UUID id, SeguroRequest request) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seguro não encontrado"));

        validarSeguro(request.titulo(), request.valorPremioBase());

        seguro.setTitulo(request.titulo());
        seguro.setCoberturaMinima(request.coberturaMinima());
        seguro.setValorPremioBase(request.valorPremioBase());

        Seguro atualizado = seguroRepository.save(seguro);
        return SeguroResponse.fromEntity(atualizado);
    }

    public void excluir(UUID id) {
        if (!seguroRepository.existsById(id)) {
            throw new IllegalArgumentException("Seguro não encontrado");
        }
        seguroRepository.deleteById(id);
    }

    public SeguroResponse buscarPorId(UUID id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seguro não encontrado"));
        return SeguroResponse.fromEntity(seguro);
    }

    public List<SeguroResponse> listarTodos() {
        return seguroRepository.findAll()
                .stream()
                .map(SeguroResponse::fromEntity)
                .toList();
    }

    // RF03 - Validação de Seguro
    public void validarSeguro(String titulo, BigDecimal valorPremioBase) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (valorPremioBase == null || valorPremioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de Prêmio Base deve ser um número decimal positivo");
        }
    }
}
