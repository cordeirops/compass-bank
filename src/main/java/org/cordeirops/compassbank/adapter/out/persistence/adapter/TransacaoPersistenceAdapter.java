package org.cordeirops.compassbank.adapter.out.persistence.adapter;

import org.cordeirops.compassbank.adapter.out.persistence.entity.TransacaoEntity;
import org.cordeirops.compassbank.adapter.out.persistence.repository.TransacaoJpaRepository;
import org.cordeirops.compassbank.application.port.out.TransacaoRepositoryPort;
import org.cordeirops.compassbank.domain.model.Transacao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransacaoPersistenceAdapter implements TransacaoRepositoryPort {

    private final TransacaoJpaRepository transacaoJpaRepository;

    public TransacaoPersistenceAdapter(TransacaoJpaRepository transacaoJpaRepository) {
        this.transacaoJpaRepository = transacaoJpaRepository;
    }

    @Override
    public Transacao save(Transacao transacao) {
        return toDomain(transacaoJpaRepository.save(toEntity(transacao)));
    }

    @Override
    public List<Transacao> findByContaId(UUID contaId) {
        return transacaoJpaRepository
                .findByContaOrigemIdOrContaDestinoIdOrderByCriadaEmDesc(contaId, contaId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TransacaoEntity toEntity(Transacao t) {
        TransacaoEntity entity = new TransacaoEntity();
        entity.setId(t.getId());
        entity.setTransferenciaId(t.getTransferenciaId());
        entity.setContaOrigemId(t.getContaOrigemId());
        entity.setContaDestinoId(t.getContaDestinoId());
        entity.setValor(t.getValor());
        entity.setTipo(t.getTipo());
        entity.setDescricao(t.getDescricao());
        return entity;
    }

    private Transacao toDomain(TransacaoEntity e) {
        return new Transacao(
                e.getId(),
                e.getTransferenciaId(),
                e.getContaOrigemId(),
                e.getContaDestinoId(),
                e.getValor(),
                e.getTipo(),
                e.getCriadaEm(),
                e.getDescricao()
        );
    }
}
