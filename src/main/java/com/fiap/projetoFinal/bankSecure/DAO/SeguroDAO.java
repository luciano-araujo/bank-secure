package com.fiap.projetoFinal.bankSecure.DAO;

import com.fiap.projetoFinal.bankSecure.Repository.SeguroRepository;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class SeguroDAO {

    @NonNull
    private final SeguroRepository seguroRepository;
}
