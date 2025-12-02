package com.fiap.projetoFinal.banksecure.repository;

import com.fiap.projetoFinal.banksecure.domain.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro,Long> {
}
