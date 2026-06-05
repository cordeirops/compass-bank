package org.cordeirops.compassbank.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarContaRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "Saldo inicial é obrigatório")
        @DecimalMin(value = "0.00", message = "Saldo inicial não pode ser negativo")
        BigDecimal saldoInicial
) {}
