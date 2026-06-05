package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.dto.TransferenciaResultado;
import org.cordeirops.compassbank.application.port.in.RealizarTransferenciaUseCase;
import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.application.port.out.NotificacaoTransferenciaPort;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.exception.MesmaContaTransferenciaException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.TipoTransacao;
import org.cordeirops.compassbank.domain.model.Transacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferenciaService implements RealizarTransferenciaUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);

    private final ContaRepositoryPort contaRepositoryPort;
    private final TransacaoRepositoryPort transacaoRepositoryPort;
    private final NotificacaoTransferenciaPort notificacaoPort;

    public TransferenciaService(ContaRepositoryPort contaRepositoryPort,
                                TransacaoRepositoryPort transacaoRepositoryPort,
                                NotificacaoTransferenciaPort notificacaoPort) {
        this.contaRepositoryPort = contaRepositoryPort;
        this.transacaoRepositoryPort = transacaoRepositoryPort;
        this.notificacaoPort = notificacaoPort;
    }

    @Transactional
    @Override
    public TransferenciaResultado transferir(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor) {
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
                UUID.randomUUID(), contaOrigemId, contaDestinoId,
                valor, TipoTransacao.DEBITO, agora,
                "Transferência enviada para conta " + contaDestinoId
        ));
        transacaoRepositoryPort.save(new Transacao(
                UUID.randomUUID(), contaOrigemId, contaDestinoId,
                valor, TipoTransacao.CREDITO, agora,
                "Transferência recebida da conta " + contaOrigemId
        ));

        try {
            notificacaoPort.notificar(new TransferenciaConcluidaEvent(
                    transferId, contaOrigemId, contaDestinoId, valor, agora
            ));
        } catch (Exception e) {
            log.error("Falha ao notificar transferência {}: {}", transferId, e.getMessage());
        }

        return new TransferenciaResultado(transferId, contaOrigemId, contaDestinoId, valor, agora);
    }
}
