package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.application.dto.TransferenciaResultadoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaResponseDTO(
        UUID transferenciaId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        LocalDateTime executadaEm
) {
    public static TransferenciaResponseDTO from(TransferenciaResultadoDTO resultado) {
        return new TransferenciaResponseDTO(
                resultado.transferenciaId(),
                resultado.contaOrigemId(),
                resultado.contaDestinoId(),
                resultado.valor(),
                resultado.executadaEm()
        );
    }
}
