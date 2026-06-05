package org.cordeirops.compassbank.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI compassBankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Compass Bank API")
                        .description("""
                                API REST para banco digital com:
                                - Gestão de contas bancárias
                                - Transferência de valores com consistência em alta concorrência (lock pessimista + threads virtuais)
                                - Notificações assíncronas via Apache Kafka
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pedro S Cordeiro")
                                .email("pedro.sbarainicordeiro@gmail.com")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório GitHub")
                        .url("https://github.com/cordeirops/compass-bank")
                );
    }
}
