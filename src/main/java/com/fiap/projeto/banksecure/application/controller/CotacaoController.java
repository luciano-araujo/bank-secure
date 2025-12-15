package com.fiap.projeto.banksecure.application.controller;

import com.fiap.projeto.banksecure.application.dto.CotacaoDTO;
import com.fiap.projeto.banksecure.application.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
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
    public ResponseEntity<CotacaoDTO> realizarCotacao(@Valid @RequestBody CotacaoDTO request) {
        return ResponseEntity.ok(cotacaoService.calcularCotacao(request.clienteId(), request.seguroId()));
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
