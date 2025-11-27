package com.fiap.projetoFinal.bankSecure.DAO;

import com.fiap.projetoFinal.bankSecure.Repository.ApoliceRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Data
@AllArgsConstructor
public class ApoliceDAO {

    @NonNull
    private final ApoliceRepository apoliceRepository;
}
