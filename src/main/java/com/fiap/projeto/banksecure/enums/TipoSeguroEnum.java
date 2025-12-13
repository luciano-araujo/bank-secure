package com.fiap.projeto.banksecure.enums;

public enum TipoSeguroEnum {
    RESIDENCIAL(0.02),
    AUTOMOTIVO(0.03),
    VIDA(0.01);

    private final double taxa;

    TipoSeguroEnum(double taxa) {
        this.taxa = taxa;
    }

    public double getTaxa() {
        return taxa;
    }
}

