package com.fiap.projetoFinal.bankSecure.Repository;

import com.fiap.projetoFinal.bankSecure.Domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {
}
