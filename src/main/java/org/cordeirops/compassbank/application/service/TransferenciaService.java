package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.dto.TransferenciaResultadoDTO;
import org.cordeirops.compassbank.application.port.in.RealizarTransferenciaUseCase;
import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.exception.MesmaContaTransferenciaException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.TipoTransacao;
import org.cordeirops.compassbank.domain.model.Transacao;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferenciaService implements RealizarTransferenciaUseCase {

    private final ContaRepositoryPort contaRepositoryPort;
    private final TransacaoRepositoryPort transacaoRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    public TransferenciaService(ContaRepositoryPort contaRepositoryPort,
                                TransacaoRepositoryPort transacaoRepositoryPort,
                                ApplicationEventPublisher eventPublisher) {
        this.contaRepositoryPort = contaRepositoryPort;
        this.transacaoRepositoryPort = transacaoRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public TransferenciaResultadoDTO transferir(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor) {
        if (contaOrigemId.equals(contaDestinoId)) {
            throw new MesmaContaTransferenciaException(contaOrigemId);
        }

        // Locks sempre adquiridos em ordem crescente de UUID para prevenir deadlocks.
        // Thread A: origem=1, destino=2 → trava 1, depois 2.
        // Thread B: origem=2, destino=1 → trava 1, depois 2 (mesma ordem).
        // Resultado: threads competem pelo mesmo lock primeiro, eliminando espera circular.
        UUID primeiroId = contaOrigemId.compareTo(contaDestinoId) < 0 ? contaOrigemId : contaDestinoId;
        UUID segundoId  = contaOrigemId.compareTo(contaDestinoId) < 0 ? contaDestinoId : contaOrigemId;

        Conta primeiro = contaRepositoryPort.findByIdComLock(primeiroId)
                .orElseThrow(() -> new ContaNaoEncontradaException(primeiroId));
        Conta segundo  = contaRepositoryPort.findByIdComLock(segundoId)
                .orElseThrow(() -> new ContaNaoEncontradaException(segundoId));

        Conta origem  = primeiroId.equals(contaOrigemId) ? primeiro : segundo;
        Conta destino = primeiroId.equals(contaDestinoId) ? primeiro : segundo;

        origem.debitar(valor);
        destino.creditar(valor);

        contaRepositoryPort.save(origem);
        contaRepositoryPort.save(destino);

        UUID transferId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        transacaoRepositoryPort.save(new Transacao(
                UUID.randomUUID(), transferId, contaOrigemId, contaDestinoId,
                valor, TipoTransacao.DEBITO, agora,
                "Transferência enviada para conta " + contaDestinoId
        ));
        transacaoRepositoryPort.save(new Transacao(
                UUID.randomUUID(), transferId, contaOrigemId, contaDestinoId,
                valor, TipoTransacao.CREDITO, agora,
                "Transferência recebida da conta " + contaOrigemId
        ));

        // Publicado após o commit via @TransactionalEventListener(AFTER_COMMIT) em TransferenciaEventListener
        eventPublisher.publishEvent(new TransferenciaConcluidaEvent(
                transferId, contaOrigemId, contaDestinoId, valor, agora
        ));

        return new TransferenciaResultadoDTO(transferId, contaOrigemId, contaDestinoId, valor, agora);
    }
}
