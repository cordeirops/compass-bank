package org.cordeirops.compassbank.application.port.out;

import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;

public interface NotificacaoTransferenciaPort {

    void notificar(TransferenciaConcluidaEvent evento);
}
