package org.cordeirops.compassbank.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaConcluidaEvent(
        UUID transferenciaId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        LocalDateTime ocorridaEm
) {}
