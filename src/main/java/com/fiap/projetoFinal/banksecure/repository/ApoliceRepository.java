package com.fiap.projetoFinal.banksecure.repository;

import com.fiap.projetoFinal.banksecure.domain.Apolice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApoliceRepository extends JpaRepository<Apolice,Long> {
}
