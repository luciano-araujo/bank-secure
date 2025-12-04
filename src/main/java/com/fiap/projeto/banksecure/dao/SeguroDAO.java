package com.fiap.projeto.banksecure.dao;

import com.fiap.projeto.banksecure.repository.SeguroRepository;
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
