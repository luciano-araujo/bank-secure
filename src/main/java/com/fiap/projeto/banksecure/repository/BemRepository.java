package com.fiap.projeto.banksecure.repository;

import com.fiap.projeto.banksecure.domain.Bem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface BemRepository extends JpaRepository<Bem, UUID> {
    boolean existsByIdAndApoliceIsNotNull(UUID id);
}
