package com.fiap.projeto.banksecure.repository;

import com.fiap.projeto.banksecure.domain.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, UUID> {
}
