package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.domain.model.Conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponseDTO(
        UUID id,
        String nome,
        BigDecimal saldo
) {
    public static ContaResponseDTO from(Conta conta) {
        return new ContaResponseDTO(conta.getId(), conta.getNome(), conta.getSaldo());
    }
}
