package com.fiap.projeto.banksecure.controller;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Cliente;
import com.fiap.projeto.banksecure.dto.ApoliceDTO;
import com.fiap.projeto.banksecure.enums.TipoSeguroEnum;
import com.fiap.projeto.banksecure.service.ApoliceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apolice")
@RequiredArgsConstructor
public class ApoliceController {

    private final ApoliceService apoliceService;

    @PostMapping
    public ResponseEntity<ApoliceDTO> criarApolice(@RequestBody ApoliceDTO apoliceDTO){
        if(apoliceDTO == null){
            return ResponseEntity.badRequest().build();
        }

        try{
            Apolice apolice = apoliceService.criarApolice(
                    apoliceDTO.toEntity().getCliente(),
                    apoliceDTO.toEntity().getTipoSeguroEnum(),
                    apoliceDTO.toEntity().getListaDeBens()
            );
            return ResponseEntity.ok(apoliceDTO);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/renovacao")
    public ResponseEntity<Apolice> renovarApolice(@RequestBody Apolice apoliceAntiga) {
        try {
            Apolice apoliceRenovada = apoliceService.renovarApolice(apoliceAntiga);
            return ResponseEntity.status(HttpStatus.CREATED).body(apoliceRenovada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/dashboard")
    public ResponseEntity<Map<TipoSeguroEnum, ApoliceService.ResumoDashboard>> getDashboard(@RequestBody List<Apolice> apolices){
        Map<TipoSeguroEnum, ApoliceService.ResumoDashboard> dashboard = apoliceService.dashboardPorTipo(apolices);
        return ResponseEntity.ok(dashboard);
    }

}
