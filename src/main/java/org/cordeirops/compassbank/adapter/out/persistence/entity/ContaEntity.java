package org.cordeirops.compassbank.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contas")
public class ContaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldo;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @UpdateTimestamp
    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm;

    public ContaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getAtualizadaEm() { return atualizadaEm; }
}
