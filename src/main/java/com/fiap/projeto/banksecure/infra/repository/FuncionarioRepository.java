package com.fiap.projeto.banksecure.infra.repository;

import com.fiap.projeto.banksecure.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
    Optional<Funcionario> findByEmail(String email);
}
