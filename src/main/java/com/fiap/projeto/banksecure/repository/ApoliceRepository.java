package com.fiap.projeto.banksecure.repository;

import com.fiap.projeto.banksecure.domain.Apolice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApoliceRepository extends JpaRepository<Apolice, UUID> {
}
