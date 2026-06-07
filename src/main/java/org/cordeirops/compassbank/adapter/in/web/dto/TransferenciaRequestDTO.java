package org.cordeirops.compassbank.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequestDTO(

        @NotNull(message = "ID da conta de origem é obrigatório")
        UUID contaOrigemId,

        @NotNull(message = "ID da conta de destino é obrigatório")
        UUID contaDestinoId,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor mínimo para transferência é R$ 0,01")
        BigDecimal valor
) {}
