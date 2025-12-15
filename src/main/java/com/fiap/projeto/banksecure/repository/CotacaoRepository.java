package com.fiap.projeto.banksecure.repository;

import com.fiap.projeto.banksecure.domain.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CotacaoRepository extends JpaRepository<Cotacao, UUID> {
}
