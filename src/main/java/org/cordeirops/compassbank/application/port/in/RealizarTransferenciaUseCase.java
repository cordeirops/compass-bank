package org.cordeirops.compassbank.application.port.in;

import org.cordeirops.compassbank.application.dto.TransferenciaResultadoDTO;

import java.math.BigDecimal;
import java.util.UUID;

public interface RealizarTransferenciaUseCase {

    TransferenciaResultadoDTO transferir(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor);
}
