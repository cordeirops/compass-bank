package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.application.dto.TransferenciaResultado;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaResponse(
        UUID transferenciaId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        LocalDateTime executadaEm
) {
    public static TransferenciaResponse from(TransferenciaResultado resultado) {
        return new TransferenciaResponse(
                resultado.transferenciaId(),
                resultado.contaOrigemId(),
                resultado.contaDestinoId(),
                resultado.valor(),
                resultado.executadaEm()
        );
    }
}
