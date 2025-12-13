package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.dto.CotacaoRequestDTO;
import com.fiap.projeto.banksecure.service.CotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cotacao")
@RequiredArgsConstructor
public class CotacaoController {

    private final CotacaoService cotacaoService;

    @PostMapping
    public ResponseEntity<CotacaoDTO> realizarCotacao(@Valid @RequestBody CotacaoRequestDTO request) {
        return ResponseEntity.ok(
                cotacaoService.realizarCotacao(request.clienteId(), request.seguroId(), request.coberturaTotal())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotacaoDTO> getCotacaoById(@PathVariable UUID id) {
        return ResponseEntity.ok(cotacaoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CotacaoDTO>> getAllCotacoes() {
        return ResponseEntity.ok(cotacaoService.getAllCotacoes());
    }
}
