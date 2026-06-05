package org.cordeirops.compassbank.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.cordeirops.compassbank.domain.model.TipoTransacao;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "transacoes",
    indexes = {
        @Index(name = "idx_transacoes_origem",  columnList = "conta_origem_id"),
        @Index(name = "idx_transacoes_destino", columnList = "conta_destino_id")
    }
)
public class TransacaoEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "conta_origem_id")
    private UUID contaOrigemId;

    @Column(name = "conta_destino_id")
    private UUID contaDestinoId;

    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;

    @Column(name = "descricao")
    private String descricao;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    public TransacaoEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getContaOrigemId() { return contaOrigemId; }
    public void setContaOrigemId(UUID contaOrigemId) { this.contaOrigemId = contaOrigemId; }

    public UUID getContaDestinoId() { return contaDestinoId; }
    public void setContaDestinoId(UUID contaDestinoId) { this.contaDestinoId = contaDestinoId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getCriadaEm() { return criadaEm; }
}
