package org.cordeirops.compassbank.application.port.out;

import org.cordeirops.compassbank.domain.model.Conta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContaRepositoryPort {

    List<Conta> findAll();

    Optional<Conta> findById(UUID id);

    Optional<Conta> findByIdComLock(UUID id);

    Conta save(Conta conta);

    boolean existsById(UUID id);
}
