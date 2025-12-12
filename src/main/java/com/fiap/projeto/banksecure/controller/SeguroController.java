package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.dto.SeguroRequest;
import com.fiap.projeto.banksecure.dto.SeguroResponse;
import com.fiap.projeto.banksecure.service.SeguroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seguros")
public class SeguroController {

    private final SeguroService seguroService;

    public SeguroController(SeguroService seguroService) {
        this.seguroService = seguroService;
    }

    @PostMapping
    public ResponseEntity<SeguroResponse> cadastrar(@RequestBody SeguroRequest request) {
        SeguroResponse response = seguroService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguroResponse> alterar(@PathVariable UUID id, @RequestBody SeguroRequest request) {
        SeguroResponse response = seguroService.alterar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        seguroService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguroResponse> buscarPorId(@PathVariable UUID id) {
        SeguroResponse response = seguroService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SeguroResponse>> listarTodos() {
        List<SeguroResponse> seguros = seguroService.listarTodos();
        return ResponseEntity.ok(seguros);
    }
}
