package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.dto.DashboardDTO;
import com.fiap.projeto.banksecure.dto.NovaApoliceDTO;
import com.fiap.projeto.banksecure.service.ApoliceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/apolice")
@RequiredArgsConstructor
public class ApoliceController {

    private final ApoliceService apoliceService;

    @PostMapping
    public ResponseEntity<ApoliceDTO> criarApolice(@Valid @RequestBody NovaApoliceDTO apoliceDTO) {
        return ResponseEntity.ok(apoliceService.criarApolice(apoliceDTO));
    }

    @GetMapping("/vencer")
    public ResponseEntity<List<ApoliceDTO>> listarApolicesAVencer() {
        return ResponseEntity.ok(apoliceService.listarApolicesAVencer());
    }

    @PostMapping("/renovacao/{id}")
    public ResponseEntity<ApoliceDTO> renovarApolice(@PathVariable UUID id) {
        return ResponseEntity.ok(apoliceService.renovarApolice(id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<DashboardDTO>> getDashboard() {
        return ResponseEntity.ok(apoliceService.getDashboard());
    }

    @GetMapping
    public ResponseEntity<List<ApoliceDTO>> listarTodas() {
        return ResponseEntity.ok(apoliceService.listarTodasApolices());
    }

}
