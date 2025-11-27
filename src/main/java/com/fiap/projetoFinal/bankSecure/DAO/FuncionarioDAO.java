package com.fiap.projetoFinal.bankSecure.DAO;

import com.fiap.projetoFinal.bankSecure.Repository.FuncionarioRepository;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class FuncionarioDAO {

    @NonNull
    private final FuncionarioRepository funcionarioRepository;
}
