package com.fiap.projetoFinal.bankSecure.Repository;

import com.fiap.projetoFinal.bankSecure.Domain.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro,Long> {
}
