package org.cordeirops.compassbank.application.port.out;

import org.cordeirops.compassbank.domain.model.Conta;

import java.util.Optional;
import java.util.UUID;

public interface ContaRepositoryPort {

    Optional<Conta> findById(UUID id);

    Optional<Conta> findByIdComLock(UUID id);

    Conta save(Conta conta);

    boolean existsById(UUID id);
}
