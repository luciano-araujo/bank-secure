package com.fiap.projeto.banksecure.application.controller;

import com.fiap.projeto.banksecure.application.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.infra.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.application.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/funcionario")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioRepository funciRepository;
    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> getAllFuncionarios() {
        return ResponseEntity.ok(funcionarioService.getAllFuncionarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> getFuncionarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> createFuncionario(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        return ResponseEntity.ok(funcionarioService.cadastrarFuncionario(funcionarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> updateFuncionario(@PathVariable UUID id, @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        return ResponseEntity.ok(funcionarioService.atualizarFuncionario(id, funcionarioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuncionario(@PathVariable UUID id) {
        funcionarioService.deletarFuncionario(id);
        return ResponseEntity.noContent().build();
    }
}
