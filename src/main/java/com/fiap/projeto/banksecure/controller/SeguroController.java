package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.dto.SeguroDTO;
import com.fiap.projeto.banksecure.service.SeguroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/seguro")
@RequiredArgsConstructor
public class SeguroController {

    private final SeguroService seguroService;

    @GetMapping
    public ResponseEntity<List<SeguroDTO>> getAllSeguros() {
        return ResponseEntity.ok(seguroService.getAllSeguros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguroDTO> getSeguroById(@PathVariable UUID id) {
        return ResponseEntity.ok(seguroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SeguroDTO> createSeguro(@RequestBody SeguroDTO seguroDTO) {
        return ResponseEntity.ok(seguroService.cadastrarSeguro(seguroDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguroDTO> updateSeguro(@PathVariable UUID id, @RequestBody SeguroDTO seguroDTO) {
        return ResponseEntity.ok(seguroService.atualizarSeguro(id, seguroDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguro(@PathVariable UUID id) {
        seguroService.deletarSeguro(id);
        return ResponseEntity.noContent().build();
    }
}
