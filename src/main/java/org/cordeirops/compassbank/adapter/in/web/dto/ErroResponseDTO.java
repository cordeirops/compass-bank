package org.cordeirops.compassbank.adapter.in.web.dto;

import java.time.LocalDateTime;

public record ErroResponseDTO(
        int status,
        String erro,
        String mensagem,
        LocalDateTime timestamp,
        String caminho
) {}
