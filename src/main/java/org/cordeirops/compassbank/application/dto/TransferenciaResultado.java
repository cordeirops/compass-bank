package org.cordeirops.compassbank.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaResultado(
        UUID transferenciaId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        LocalDateTime executadaEm
) {}
