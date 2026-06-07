package org.cordeirops.compassbank.adapter.out.messaging;

import org.cordeirops.compassbank.domain.event.TransferenciaConcluidaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransferenciaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransferenciaEventConsumer.class);

    @KafkaListener(
            topics = KafkaNotificacaoTransferenciaAdapter.TOPICO,
            groupId = "compass-bank-notificacoes"
    )
    public void consumir(TransferenciaConcluidaEvent evento) {
        log.info(
                "Notificação recebida: transferência {} | origem: {} → destino: {} | valor: R$ {} | em: {}",
                evento.transferenciaId(),
                evento.contaOrigemId(),
                evento.contaDestinoId(),
                evento.valor(),
                evento.ocorridaEm()
        );
        // Extensível: envio de e-mail, SMS, push notification, análise de fraude, etc.
    }
}
