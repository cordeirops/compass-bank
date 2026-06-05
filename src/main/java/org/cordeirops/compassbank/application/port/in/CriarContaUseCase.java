package org.cordeirops.compassbank.application.port.in;

import org.cordeirops.compassbank.domain.model.Conta;

import java.math.BigDecimal;

public interface CriarContaUseCase {

    Conta criarConta(String nome, BigDecimal saldoInicial);
}
