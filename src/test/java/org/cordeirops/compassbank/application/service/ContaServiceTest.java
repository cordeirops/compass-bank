package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.TipoTransacao;
import org.cordeirops.compassbank.domain.model.Transacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock ContaRepositoryPort contaRepositoryPort;
    @Mock TransacaoRepositoryPort transacaoRepositoryPort;

    @InjectMocks ContaService contaService;

    @Test
    void criarConta_dadosValidos_retornaConta() {
        Conta contaSalva = new Conta(UUID.randomUUID(), "Ana Silva", new BigDecimal("1000.00"));
        when(contaRepositoryPort.save(any())).thenReturn(contaSalva);

        Conta resultado = contaService.criarConta("Ana Silva", new BigDecimal("1000.00"));

        assertThat(resultado.getNome()).isEqualTo("Ana Silva");
        assertThat(resultado.getSaldo()).isEqualByComparingTo("1000.00");
        verify(contaRepositoryPort).save(any());
    }

    @Test
    void buscarConta_contaExistente_retornaConta() {
        UUID id = UUID.randomUUID();
        Conta conta = new Conta(id, "Ana Silva", new BigDecimal("1000.00"));
        when(contaRepositoryPort.findById(id)).thenReturn(Optional.of(conta));

        Conta resultado = contaService.buscarConta(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNome()).isEqualTo("Ana Silva");
    }

    @Test
    void buscarConta_contaInexistente_lancaExcecao() {
        UUID id = UUID.randomUUID();
        when(contaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaService.buscarConta(id))
                .isInstanceOf(ContaNaoEncontradaException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void buscarHistorico_contaInexistente_lancaExcecao() {
        UUID id = UUID.randomUUID();
        when(transacaoRepositoryPort.findByContaId(id)).thenReturn(List.of());
        when(contaRepositoryPort.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> contaService.buscarHistorico(id))
                .isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void listarContas_retornaTodasAsContas() {
        List<Conta> contas = List.of(
                new Conta(UUID.randomUUID(), "Ana Silva", new BigDecimal("1000.00")),
                new Conta(UUID.randomUUID(), "Bob Santos", new BigDecimal("500.00"))
        );
        when(contaRepositoryPort.findAll()).thenReturn(contas);

        List<Conta> resultado = contaService.listarContas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Ana Silva");
    }

    @Test
    void criarConta_saldoNegativo_lancaIllegalArgumentException() {
        assertThatThrownBy(() -> contaService.criarConta("Ana", new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(contaRepositoryPort, never()).save(any());
    }

    @Test
    void buscarHistorico_contaComTransacoes_naoVerificaExistenciaNoDb() {
        UUID contaId = UUID.randomUUID();
        Transacao transacao = new Transacao(
                UUID.randomUUID(), UUID.randomUUID(), contaId, UUID.randomUUID(),
                new BigDecimal("200.00"), TipoTransacao.DEBITO,
                LocalDateTime.now(), "Transferência enviada"
        );
        when(transacaoRepositoryPort.findByContaId(contaId)).thenReturn(List.of(transacao));

        contaService.buscarHistorico(contaId);

        verify(contaRepositoryPort, never()).existsById(any());
    }

    @Test
    void buscarHistorico_contaExistente_retornaLista() {
        UUID contaId = UUID.randomUUID();
        Transacao transacao = new Transacao(
                UUID.randomUUID(), UUID.randomUUID(), contaId, UUID.randomUUID(),
                new BigDecimal("200.00"), TipoTransacao.DEBITO,
                LocalDateTime.now(), "Transferência enviada"
        );
        when(transacaoRepositoryPort.findByContaId(contaId)).thenReturn(List.of(transacao));

        List<Transacao> resultado = contaService.buscarHistorico(contaId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getTipo()).isEqualTo(TipoTransacao.DEBITO);
    }
}
