package org.cordeirops.compassbank.domain.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(UUID id, BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super("Saldo insuficiente na conta " + id);
    }
}
