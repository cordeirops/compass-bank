package org.cordeirops.compassbank.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaAdmin.NewTopics topicos() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("notificacoes-transferencia")
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}
