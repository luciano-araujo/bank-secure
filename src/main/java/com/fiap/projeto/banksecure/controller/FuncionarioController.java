package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.domain.Funcionario;
import com.fiap.projeto.banksecure.dto.AuthRequest;
import com.fiap.projeto.banksecure.dto.AuthResponse;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
import com.fiap.projeto.banksecure.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticateFuncionario(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(funcionarioService.logar(authRequest));
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> getAllFuncionarios() {
        return ResponseEntity.ok(funcionarioService.getAllFuncionarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> getFuncionarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> createFuncionario(@RequestBody FuncionarioDTO funcionarioDTO) {
        return ResponseEntity.ok(funcionarioService.cadastrarFuncionario(funcionarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> updateFuncionario(@PathVariable UUID id, @RequestBody FuncionarioDTO funcionarioDTO) {
        return ResponseEntity.ok(funcionarioService.atualizarFuncionario(id, funcionarioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuncionario(@PathVariable UUID id) {
        funcionarioService.deletarFuncionario(id);
        return ResponseEntity.noContent().build();
    }
}