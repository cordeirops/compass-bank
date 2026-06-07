package org.cordeirops.compassbank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"notificacoes-transferencia"})
class CompassBankApplicationTests {

    @Test
    void contextLoads() {
    }
}
