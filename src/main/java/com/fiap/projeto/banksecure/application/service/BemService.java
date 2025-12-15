package com.fiap.projeto.banksecure.application.service;

import com.fiap.projeto.banksecure.domain.entity.Apolice;
import com.fiap.projeto.banksecure.domain.entity.Bem;
import com.fiap.projeto.banksecure.application.dto.BemDTO;
import com.fiap.projeto.banksecure.infra.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.infra.repository.BemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BemService {

    private final BemRepository bemRepository;
    private final ApoliceRepository apoliceRepository;

    public BemDTO cadastrarBem(BemDTO bemDTO) {
        Bem bem = bemDTO.toEntity();

        Apolice apolice = apoliceRepository.findById(bemDTO.apoliceId())
                .orElseThrow(() -> new IllegalArgumentException("Apólice não encontrada"));

        bem.setApolice(apolice);

        validarBem(bem);
        Bem salvo = bemRepository.save(bem);

        return BemDTO.fromEntity(salvo);
    }

    public BemDTO atualizarBem(UUID id, BemDTO bemDTO) {
        Bem bemExistente = bemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bem não encontrado com o ID: " + id));

        bemExistente.setTitulo(bemDTO.titulo());
        bemExistente.setValor(bemDTO.valor());

        validarBem(bemExistente);
        Bem bemAtualizado = bemRepository.save(bemExistente);

        return BemDTO.fromEntity(bemAtualizado);
    }

    public void deletarBem(UUID id) {
        if (bemRepository.existsByIdAndApoliceIsNotNull(id)) {
            throw new IllegalStateException("Não é possível excluir bem vinculado a uma apólice");
        }

        Bem bem = bemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bem não encontrado"));

        bemRepository.delete(bem);
    }

    public BemDTO buscarPorId(UUID id) {
        Bem bem = bemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bem não encontrado"));

        return BemDTO.fromEntity(bem);
    }

    public List<BemDTO> getAllBens() {
        return bemRepository.findAll().stream()
                .map(BemDTO::fromEntity)
                .toList();
    }

    protected void validarBem(Bem bem) {
        if (bem == null) {
            throw new IllegalArgumentException("Bem é obrigatório.");
        }

        if (bem.getTitulo() == null || bem.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título do bem é obrigatório.");
        }

        if (bem.getValor() == null) {
            throw new IllegalArgumentException("Valor do bem é obrigatório.");
        }

        if (BigDecimal.ZERO.compareTo(bem.getValor()) >= 0) {
            throw new IllegalArgumentException("Valor do bem deve ser maior que zero.");
        }

        if (bem.getApolice() == null || bem.getApolice().getId() == null) {
            throw new IllegalArgumentException("Apólice é obrigatória para o bem.");
        }

        if (!apoliceRepository.existsById(bem.getApolice().getId())) {
            throw new IllegalArgumentException("Apólice não encontrada.");
        }
    }
}
