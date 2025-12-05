package com.fiap.projeto.banksecure.repository;

import com.fiap.projeto.banksecure.domain.Bem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BemRepository extends JpaRepository<Bem, UUID> {
}
