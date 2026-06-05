package org.cordeirops.compassbank.adapter.out.messaging;

import org.cordeirops.compassbank.application.port.out.NotificacaoTransferenciaPort;
import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificacaoTransferenciaAdapter implements NotificacaoTransferenciaPort {

    static final String TOPICO = "notificacoes-transferencia";

    private final KafkaTemplate<String, TransferenciaConcluidaEvent> kafkaTemplate;

    public KafkaNotificacaoTransferenciaAdapter(
            KafkaTemplate<String, TransferenciaConcluidaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notificar(TransferenciaConcluidaEvent evento) {
        // Chave = contaOrigemId → eventos da mesma conta ficam na mesma partição (ordenação garantida)
        kafkaTemplate.send(TOPICO, evento.contaOrigemId().toString(), evento);
    }
}
