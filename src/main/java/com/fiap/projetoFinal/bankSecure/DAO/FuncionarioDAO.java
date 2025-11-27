package com.fiap.projetoFinal.bankSecure.DAO;

import com.fiap.projetoFinal.bankSecure.Repository.FuncionarioRepository;
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
