package org.cordeirops.compassbank.application.port.in;

import org.cordeirops.compassbank.application.dto.TransferenciaResultado;

import java.math.BigDecimal;
import java.util.UUID;

public interface RealizarTransferenciaUseCase {

    TransferenciaResultado transferir(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor);
}
