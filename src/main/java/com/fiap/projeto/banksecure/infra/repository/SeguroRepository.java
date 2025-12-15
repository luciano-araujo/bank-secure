package com.fiap.projeto.banksecure.infra.repository;

import com.fiap.projeto.banksecure.domain.entity.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, UUID> {
    Optional<Seguro> findByTitulo(String titulo);
    boolean existsByTitulo(String titulo);
}