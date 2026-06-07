package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.port.out.NotificacaoTransferenciaPort;
import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferenciaEventListenerTest {

    @Mock NotificacaoTransferenciaPort notificacaoPort;
    @InjectMocks TransferenciaEventListener listener;

    @Test
    void aoReceberEvento_notificaViaPort() {
        TransferenciaConcluidaEvent evento = new TransferenciaConcluidaEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("200.00"), LocalDateTime.now()
        );

        listener.onTransferenciaConcluida(evento);

        verify(notificacaoPort).notificar(evento);
    }
}
