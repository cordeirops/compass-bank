package org.cordeirops.compassbank.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cordeirops.compassbank.adapter.in.web.dto.TransferenciaRequest;
import org.cordeirops.compassbank.application.dto.TransferenciaResultado;
import org.cordeirops.compassbank.application.port.in.RealizarTransferenciaUseCase;
import org.cordeirops.compassbank.config.GlobalExceptionHandler;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.exception.MesmaContaTransferenciaException;
import org.cordeirops.compassbank.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferenciaControllerTest {

    @Mock RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private static final UUID ORIGEM_ID  = UUID.randomUUID();
    private static final UUID DESTINO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransferenciaController(realizarTransferenciaUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void transferir_dadosValidos_retorna201() throws Exception {
        TransferenciaResultado resultado = new TransferenciaResultado(
                UUID.randomUUID(), ORIGEM_ID, DESTINO_ID,
                new BigDecimal("200.00"), LocalDateTime.now()
        );
        when(realizarTransferenciaUseCase.transferir(any(), any(), any())).thenReturn(resultado);

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(ORIGEM_ID, DESTINO_ID, new BigDecimal("200.00"))
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contaOrigemId").value(ORIGEM_ID.toString()))
                .andExpect(jsonPath("$.contaDestinoId").value(DESTINO_ID.toString()));
    }

    @Test
    void transferir_saldoInsuficiente_retorna422() throws Exception {
        when(realizarTransferenciaUseCase.transferir(any(), any(), any()))
                .thenThrow(new SaldoInsuficienteException(ORIGEM_ID, BigDecimal.TEN, new BigDecimal("500.00")));

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(ORIGEM_ID, DESTINO_ID, new BigDecimal("500.00"))
                        )))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void transferir_contaNaoEncontrada_retorna404() throws Exception {
        when(realizarTransferenciaUseCase.transferir(any(), any(), any()))
                .thenThrow(new ContaNaoEncontradaException(ORIGEM_ID));

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(ORIGEM_ID, DESTINO_ID, new BigDecimal("100.00"))
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    void transferir_mesmaConta_retorna400() throws Exception {
        when(realizarTransferenciaUseCase.transferir(any(), any(), any()))
                .thenThrow(new MesmaContaTransferenciaException(ORIGEM_ID));

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(ORIGEM_ID, ORIGEM_ID, new BigDecimal("100.00"))
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void transferir_valorZero_retorna400PorValidacao() throws Exception {
        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(ORIGEM_ID, DESTINO_ID, BigDecimal.ZERO)
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferir_semContaOrigem_retorna400PorValidacao() throws Exception {
        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"contaDestinoId": "%s", "valor": 100}
                                """.formatted(DESTINO_ID)))
                .andExpect(status().isBadRequest());
    }
}
