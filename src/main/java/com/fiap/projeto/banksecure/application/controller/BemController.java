package com.fiap.projeto.banksecure.application.controller;

import com.fiap.projeto.banksecure.application.dto.BemDTO;
import com.fiap.projeto.banksecure.infra.repository.BemRepository;
import com.fiap.projeto.banksecure.application.service.BemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bem")
@RequiredArgsConstructor
public class BemController {
    private final BemService bemService;
    private final BemRepository bemRepository;

    @GetMapping
    public ResponseEntity<List<BemDTO>> getAllBens() {
        return ResponseEntity.ok(bemService.getAllBens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BemDTO> getBemById(@PathVariable UUID id) {
        return ResponseEntity.ok(bemService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BemDTO> createBem(@RequestBody BemDTO bemDTO) {
        return ResponseEntity.ok(bemService.cadastrarBem(bemDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BemDTO> updateBem(@PathVariable UUID id, @RequestBody BemDTO bemDTO) {
        return ResponseEntity.ok(bemService.atualizarBem(id, bemDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBem(@PathVariable UUID id) {
        bemService.deletarBem(id);
        return ResponseEntity.noContent().build();
    }

}
