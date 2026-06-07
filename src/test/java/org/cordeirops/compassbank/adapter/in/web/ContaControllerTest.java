package org.cordeirops.compassbank.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cordeirops.compassbank.adapter.in.web.dto.CriarContaRequestDTO;
import org.cordeirops.compassbank.application.port.in.ConsultarContaUseCase;
import org.cordeirops.compassbank.application.port.in.CriarContaUseCase;
import org.cordeirops.compassbank.config.GlobalExceptionHandler;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.TipoTransacao;
import org.cordeirops.compassbank.domain.model.Transacao;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock CriarContaUseCase criarContaUseCase;
    @Mock ConsultarContaUseCase consultarContaUseCase;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ContaController(criarContaUseCase, consultarContaUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void criar_dadosValidos_retorna201ComNome() throws Exception {
        UUID id = UUID.randomUUID();
        when(criarContaUseCase.criarConta(any(), any()))
                .thenReturn(new Conta(id, "Ana Silva", new BigDecimal("1000.00")));

        mockMvc.perform(post("/api/v1/contas")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CriarContaRequestDTO("Ana Silva", new BigDecimal("1000.00"))
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    @Test
    void criar_semNome_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/contas")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"saldoInicial": 1000}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criar_saldoNegativo_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/contas")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"nome": "Ana", "saldoInicial": -100}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscar_contaExistente_retorna200() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultarContaUseCase.buscarConta(id))
                .thenReturn(new Conta(id, "Ana Silva", new BigDecimal("1000.00")));

        mockMvc.perform(get("/api/v1/contas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Ana Silva"));
    }

    @Test
    void buscar_contaInexistente_retorna404() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultarContaUseCase.buscarConta(id))
                .thenThrow(new ContaNaoEncontradaException(id));

        mockMvc.perform(get("/api/v1/contas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listar_retorna200ComListaDeContas() throws Exception {
        List<Conta> contas = List.of(
                new Conta(UUID.randomUUID(), "Ana Silva", new BigDecimal("1000.00")),
                new Conta(UUID.randomUUID(), "Bob Santos", new BigDecimal("500.00"))
        );
        when(consultarContaUseCase.listarContas()).thenReturn(contas);

        mockMvc.perform(get("/api/v1/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Ana Silva"))
                .andExpect(jsonPath("$[1].nome").value("Bob Santos"));
    }

    @Test
    void historico_contaExistente_retorna200ComLista() throws Exception {
        UUID contaId = UUID.randomUUID();
        Transacao transacao = new Transacao(
                UUID.randomUUID(), UUID.randomUUID(), contaId, UUID.randomUUID(),
                new BigDecimal("200.00"), TipoTransacao.DEBITO,
                LocalDateTime.now(), "Transferência enviada"
        );
        when(consultarContaUseCase.buscarHistorico(eq(contaId))).thenReturn(List.of(transacao));

        mockMvc.perform(get("/api/v1/contas/{id}/transacoes", contaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipo").value("DEBITO"));
    }
}
