package org.cordeirops.compassbank.application.service;

import org.cordeirops.compassbank.application.dto.TransferenciaResultadoDTO;
import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.application.port.out.NotificacaoTransferenciaPort;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.exception.MesmaContaTransferenciaException;
import org.cordeirops.compassbank.domain.exception.SaldoInsuficienteException;
import org.cordeirops.compassbank.domain.model.Conta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock ContaRepositoryPort contaRepositoryPort;
    @Mock TransacaoRepositoryPort transacaoRepositoryPort;
    @Mock NotificacaoTransferenciaPort notificacaoPort;

    @InjectMocks TransferenciaService transferenciaService;

    // UUID menor vem primeiro na ordem lexicográfica (prevenção de deadlock)
    private static final UUID ORIGEM_ID  = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private static final UUID DESTINO_ID = UUID.fromString("a0000000-0000-0000-0000-000000000002");

    @Test
    void transferir_sucesso_debitaOrigemECreditaDestino() {
        Conta origem  = new Conta(ORIGEM_ID,  "Ana",   new BigDecimal("1000.00"));
        Conta destino = new Conta(DESTINO_ID, "Bruno", new BigDecimal("500.00"));

        when(contaRepositoryPort.findByIdComLock(ORIGEM_ID)).thenReturn(Optional.of(origem));
        when(contaRepositoryPort.findByIdComLock(DESTINO_ID)).thenReturn(Optional.of(destino));
        when(contaRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransferenciaResultadoDTO resultado = transferenciaService.transferir(
                ORIGEM_ID, DESTINO_ID, new BigDecimal("200.00")
        );

        assertThat(resultado.valor()).isEqualByComparingTo("200.00");
        assertThat(resultado.contaOrigemId()).isEqualTo(ORIGEM_ID);
        assertThat(resultado.contaDestinoId()).isEqualTo(DESTINO_ID);
        assertThat(origem.getSaldo()).isEqualByComparingTo("800.00");
        assertThat(destino.getSaldo()).isEqualByComparingTo("700.00");
        verify(transacaoRepositoryPort, times(2)).save(any());
        verify(notificacaoPort).notificar(any());
    }

    @Test
    void transferir_saldoInsuficiente_lancaExcecao() {
        Conta origem  = new Conta(ORIGEM_ID,  "Ana",   new BigDecimal("100.00"));
        Conta destino = new Conta(DESTINO_ID, "Bruno", new BigDecimal("500.00"));

        when(contaRepositoryPort.findByIdComLock(ORIGEM_ID)).thenReturn(Optional.of(origem));
        when(contaRepositoryPort.findByIdComLock(DESTINO_ID)).thenReturn(Optional.of(destino));

        assertThatThrownBy(() ->
                transferenciaService.transferir(ORIGEM_ID, DESTINO_ID, new BigDecimal("500.00"))
        ).isInstanceOf(SaldoInsuficienteException.class);

        verify(contaRepositoryPort, never()).save(any());
        verify(transacaoRepositoryPort, never()).save(any());
        verify(notificacaoPort, never()).notificar(any());
    }

    @Test
    void transferir_mesmaConta_lancaExcecao() {
        assertThatThrownBy(() ->
                transferenciaService.transferir(ORIGEM_ID, ORIGEM_ID, BigDecimal.TEN)
        ).isInstanceOf(MesmaContaTransferenciaException.class);

        verifyNoInteractions(contaRepositoryPort);
    }

    @Test
    void transferir_contaOrigemNaoEncontrada_lancaExcecao() {
        when(contaRepositoryPort.findByIdComLock(ORIGEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transferenciaService.transferir(ORIGEM_ID, DESTINO_ID, BigDecimal.TEN)
        ).isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void transferir_contaDestinoNaoEncontrada_lancaExcecao() {
        Conta origem = new Conta(ORIGEM_ID, "Ana", new BigDecimal("1000.00"));

        when(contaRepositoryPort.findByIdComLock(ORIGEM_ID)).thenReturn(Optional.of(origem));
        when(contaRepositoryPort.findByIdComLock(DESTINO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transferenciaService.transferir(ORIGEM_ID, DESTINO_ID, BigDecimal.TEN)
        ).isInstanceOf(ContaNaoEncontradaException.class);
    }

    @Test
    void transferir_ordemLockConsistente_menorUUIDSemprePrimeiro() {
        // Mesmo com a direção invertida (origem=DESTINO_ID, destino=ORIGEM_ID),
        // o lock deve ser adquirido primeiro no UUID menor (ORIGEM_ID) para prevenir deadlock.
        Conta contaMenor = new Conta(ORIGEM_ID,  "Ana",   new BigDecimal("1000.00"));
        Conta contaMaior = new Conta(DESTINO_ID, "Bruno", new BigDecimal("1000.00"));

        when(contaRepositoryPort.findByIdComLock(ORIGEM_ID)).thenReturn(Optional.of(contaMenor));
        when(contaRepositoryPort.findByIdComLock(DESTINO_ID)).thenReturn(Optional.of(contaMaior));
        when(contaRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transferenciaService.transferir(DESTINO_ID, ORIGEM_ID, new BigDecimal("100.00"));

        InOrder inOrder = inOrder(contaRepositoryPort);
        inOrder.verify(contaRepositoryPort).findByIdComLock(ORIGEM_ID);
        inOrder.verify(contaRepositoryPort).findByIdComLock(DESTINO_ID);
    }
}
