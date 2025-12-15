package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.dto.ClienteDTO;
import com.fiap.projeto.banksecure.dto.FuncionarioDTO;
import com.fiap.projeto.banksecure.repository.ClienteRepository;
import com.fiap.projeto.banksecure.service.ClienteService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllFuncionarios() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteoById(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clienteService.cadastrarCliente(clienteDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable UUID id, @Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clienteService.atualizarCliente(id, clienteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable UUID id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
