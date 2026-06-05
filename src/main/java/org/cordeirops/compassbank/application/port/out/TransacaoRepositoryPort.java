package org.cordeirops.compassbank.application.port.out;

import org.cordeirops.compassbank.domain.model.Transacao;

import java.util.List;
import java.util.UUID;

public interface TransacaoRepositoryPort {

    Transacao save(Transacao transacao);

    List<Transacao> findByContaId(UUID contaId);
}
