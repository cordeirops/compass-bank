package org.cordeirops.compassbank.adapter.in.web.dto;

import org.cordeirops.compassbank.domain.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoResponseDTO(
        UUID id,
        UUID transferenciaId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        String tipo,
        String descricao,
        LocalDateTime criadaEm
) {
    public static TransacaoResponseDTO from(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getTransferenciaId(),
                transacao.getContaOrigemId(),
                transacao.getContaDestinoId(),
                transacao.getValor(),
                transacao.getTipo().name(),
                transacao.getDescricao(),
                transacao.getCriadaEm()
        );
    }
}
