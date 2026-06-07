package org.cordeirops.compassbank.domain.model;

import org.cordeirops.compassbank.domain.exception.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.util.UUID;

public class Conta {

    private final UUID id;
    private final String nome;
    private BigDecimal saldo;

    public Conta(UUID id, String nome, BigDecimal saldo) {
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo");
        }
        this.id = id;
        this.nome = nome;
        this.saldo = saldo;
    }

    public void debitar(BigDecimal valor) {
        if (saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(id, saldo, valor);
        }
        this.saldo = saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        this.saldo = saldo.add(valor);
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getSaldo() { return saldo; }
}
