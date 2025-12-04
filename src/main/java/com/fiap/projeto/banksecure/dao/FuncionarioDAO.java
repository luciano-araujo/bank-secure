package com.fiap.projeto.banksecure.dao;

import com.fiap.projeto.banksecure.repository.FuncionarioRepository;
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
