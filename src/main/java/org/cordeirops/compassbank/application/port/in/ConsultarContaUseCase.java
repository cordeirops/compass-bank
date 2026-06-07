package org.cordeirops.compassbank.application.port.in;

import org.cordeirops.compassbank.domain.model.Conta;
import org.cordeirops.compassbank.domain.model.Transacao;

import java.util.List;
import java.util.UUID;

public interface ConsultarContaUseCase {

    List<Conta> listarContas();

    Conta buscarConta(UUID id);

    List<Transacao> buscarHistorico(UUID contaId);
}
