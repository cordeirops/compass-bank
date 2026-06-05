package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.domain.model.Conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponse(
        UUID id,
        String nome,
        BigDecimal saldo
) {
    public static ContaResponse from(Conta conta) {
        return new ContaResponse(conta.getId(), conta.getNome(), conta.getSaldo());
    }
}
