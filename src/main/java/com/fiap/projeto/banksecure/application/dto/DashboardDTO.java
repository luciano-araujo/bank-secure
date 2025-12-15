package com.fiap.projeto.banksecure.application.dto;

import java.math.BigDecimal;

public interface DashboardDTO {
    String getTipoSeguro();
    Long getQuantidadeApolices();
    BigDecimal getValorTotalArrecadado();
}
