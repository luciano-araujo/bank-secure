package com.fiap.projetoFinal.banksecure.dao;

import com.fiap.projetoFinal.banksecure.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
@AllArgsConstructor
public class ClienteDAO {

    @NonNull
    private final ClienteRepository clienteRepository;
}
