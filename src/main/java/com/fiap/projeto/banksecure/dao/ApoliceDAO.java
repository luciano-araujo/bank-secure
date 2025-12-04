package com.fiap.projeto.banksecure.dao;

import com.fiap.projeto.banksecure.repository.ApoliceRepository;
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
