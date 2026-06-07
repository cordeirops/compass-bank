package org.cordeirops.compassbank.domain.exception;

import java.util.UUID;

public class MesmaContaTransferenciaException extends RuntimeException {

    public MesmaContaTransferenciaException(UUID id) {
        super("Transferência entre a mesma conta não é permitida: " + id);
    }
}
