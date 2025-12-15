package com.fiap.projeto.banksecure.infra.repository;

import com.fiap.projeto.banksecure.domain.entity.Bem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface BemRepository extends JpaRepository<Bem, UUID> {
    boolean existsByIdAndApoliceIsNotNull(UUID id);
}
