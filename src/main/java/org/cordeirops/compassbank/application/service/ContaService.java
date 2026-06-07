package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.port.in.ConsultarContaUseCase;
import org.cordeirops.compassbank.application.port.in.CriarContaUseCase;
import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.Transacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ContaService implements CriarContaUseCase, ConsultarContaUseCase {

    private final ContaRepositoryPort contaRepositoryPort;
    private final TransacaoRepositoryPort transacaoRepositoryPort;

    public ContaService(ContaRepositoryPort contaRepositoryPort,
                        TransacaoRepositoryPort transacaoRepositoryPort) {
        this.contaRepositoryPort = contaRepositoryPort;
        this.transacaoRepositoryPort = transacaoRepositoryPort;
    }

    @Transactional
    @Override
    public Conta criarConta(String nome, BigDecimal saldoInicial) {
        Conta conta = new Conta(UUID.randomUUID(), nome, saldoInicial);
        return contaRepositoryPort.save(conta);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Conta> listarContas() {
        return contaRepositoryPort.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Conta buscarConta(UUID id) {
        return contaRepositoryPort.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Transacao> buscarHistorico(UUID contaId) {
        List<Transacao> transacoes = transacaoRepositoryPort.findByContaId(contaId);
        if (transacoes.isEmpty() && !contaRepositoryPort.existsById(contaId)) {
            throw new ContaNaoEncontradaException(contaId);
        }
        return transacoes;
    }
}
