package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.port.out.NotificacaoTransferenciaPort;
import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferenciaEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaEventListener.class);

    private final NotificacaoTransferenciaPort notificacaoPort;

    public TransferenciaEventListener(NotificacaoTransferenciaPort notificacaoPort) {
        this.notificacaoPort = notificacaoPort;
    }

    // Executado APÓS o commit bem-sucedido — garante que a notificação Kafka
    // só é enviada se a transação persistiu no banco.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferenciaConcluida(TransferenciaConcluidaEvent evento) {
        try {
            notificacaoPort.notificar(evento);
        } catch (Exception e) {
            log.error("Falha ao notificar transferência {}: {}", evento.transferenciaId(), e.getMessage());
        }
    }
}
