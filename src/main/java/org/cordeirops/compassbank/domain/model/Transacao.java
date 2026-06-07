package org.cordeirops.compassbank.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transacao {

    private final UUID id;
    private final UUID transferenciaId;
    private final UUID contaOrigemId;
    private final UUID contaDestinoId;
    private final BigDecimal valor;
    private final TipoTransacao tipo;
    private final LocalDateTime criadaEm;
    private final String descricao;

    public Transacao(UUID id, UUID transferenciaId, UUID contaOrigemId, UUID contaDestinoId,
                     BigDecimal valor, TipoTransacao tipo,
                     LocalDateTime criadaEm, String descricao) {
        this.id = id;
        this.transferenciaId = transferenciaId;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.valor = valor;
        this.tipo = tipo;
        this.criadaEm = criadaEm;
        this.descricao = descricao;
    }

    public UUID getId() { return id; }
    public UUID getTransferenciaId() { return transferenciaId; }
    public UUID getContaOrigemId() { return contaOrigemId; }
    public UUID getContaDestinoId() { return contaDestinoId; }
    public BigDecimal getValor() { return valor; }
    public TipoTransacao getTipo() { return tipo; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public String getDescricao() { return descricao; }
}
