package com.fiap.projetoFinal.bankSecure.DAO;

import com.fiap.projetoFinal.bankSecure.Repository.SeguroRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
@AllArgsConstructor
public class SeguroDAO {

    @NonNull
    private final SeguroRepository seguroRepository;
}
