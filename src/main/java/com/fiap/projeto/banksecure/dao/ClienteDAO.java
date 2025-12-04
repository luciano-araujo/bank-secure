package com.fiap.projeto.banksecure.dao;

import com.fiap.projeto.banksecure.repository.ClienteRepository;
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
