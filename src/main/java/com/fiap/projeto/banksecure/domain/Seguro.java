package com.fiap.projeto.banksecure.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "seguros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seguro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
