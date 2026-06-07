package org.cordeirops.compassbank.domain.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(UUID id, BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super(String.format(
            "Saldo insuficiente na conta %s. Saldo atual: R$ %s, valor solicitado: R$ %s",
            id, saldoAtual, valorSolicitado
        ));
    }
}
