package com.fiap.projetoFinal.banksecure.dao;

import com.fiap.projetoFinal.banksecure.repository.FuncionarioRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
@AllArgsConstructor
public class FuncionarioDAO {

    @NonNull
    private final FuncionarioRepository funcionarioRepository;
}
