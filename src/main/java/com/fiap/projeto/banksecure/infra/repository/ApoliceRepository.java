package com.fiap.projeto.banksecure.infra.repository;

import com.fiap.projeto.banksecure.domain.entity.Apolice;
import com.fiap.projeto.banksecure.application.dto.DashboardDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApoliceRepository extends JpaRepository<Apolice, UUID> {
    boolean existsByClienteId(UUID clienteId);
    List<Apolice> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query(value = "SELECT s.titulo as tipoSeguro, COUNT(a.id) as quantidadeApolices, SUM(a.premio_final) as valorTotalArrecadado " +
            "FROM apolices a JOIN seguros s ON a.seguro_id = s.id GROUP BY s.titulo", nativeQuery = true)
    List<DashboardDTO> findDashboardPorTipoSeguro();

    List<Apolice> findByDataVencimentoBefore(LocalDate data);

}
