package com.fiap.projeto.banksecure.infra.repository;

import com.fiap.projeto.banksecure.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByEmail(String email);
    boolean existsByCpf(String cpf);
}
