package org.cordeirops.compassbank.adapter.out.persistence.adapter;

import org.cordeirops.compassbank.adapter.out.persistence.entity.ContaEntity;
import org.cordeirops.compassbank.adapter.out.persistence.repository.ContaJpaRepository;
import org.cordeirops.compassbank.application.port.out.ContaRepositoryPort;
import org.cordeirops.compassbank.domain.model.Conta;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ContaPersistenceAdapter implements ContaRepositoryPort {

    private final ContaJpaRepository contaJpaRepository;

    public ContaPersistenceAdapter(ContaJpaRepository contaJpaRepository) {
        this.contaJpaRepository = contaJpaRepository;
    }

    @Override
    public Optional<Conta> findById(UUID id) {
        return contaJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Conta> findByIdComLock(UUID id) {
        return contaJpaRepository.findByIdComLock(id).map(this::toDomain);
    }

    @Override
    public Conta save(Conta conta) {
        ContaEntity entity = contaJpaRepository.findById(conta.getId())
                .orElse(new ContaEntity());
        entity.setId(conta.getId());
        entity.setNome(conta.getNome());
        entity.setSaldo(conta.getSaldo());
        return toDomain(contaJpaRepository.save(entity));
    }

    @Override
    public boolean existsById(UUID id) {
        return contaJpaRepository.existsById(id);
    }

    private Conta toDomain(ContaEntity entity) {
        return new Conta(entity.getId(), entity.getNome(), entity.getSaldo());
    }
}
