package com.fiap.projetoFinal.banksecure.repository;

import com.fiap.projetoFinal.banksecure.domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {
}
