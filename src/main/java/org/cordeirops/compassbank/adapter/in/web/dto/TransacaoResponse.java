package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.domain.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoResponse(
        UUID id,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        String tipo,
        String descricao,
        LocalDateTime criadaEm
) {
    public static TransacaoResponse from(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getContaOrigemId(),
                transacao.getContaDestinoId(),
                transacao.getValor(),
                transacao.getTipo().name(),
                transacao.getDescricao(),
                transacao.getCriadaEm()
        );
    }
}
