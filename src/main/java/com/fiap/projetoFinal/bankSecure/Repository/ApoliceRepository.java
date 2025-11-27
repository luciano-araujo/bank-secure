package com.fiap.projetoFinal.bankSecure.Repository;

import com.fiap.projetoFinal.bankSecure.Domain.Apolice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApoliceRepository extends JpaRepository<Apolice,Long> {
}
