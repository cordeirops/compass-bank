package org.cordeirops.compassbank.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.cordeirops.compassbank.adapter.in.web.dto.TransferenciaRequest;
import org.cordeirops.compassbank.adapter.in.web.dto.TransferenciaResponse;
import org.cordeirops.compassbank.application.port.in.RealizarTransferenciaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transferencias")
@Tag(name = "Transferências", description = "Transferência de valores entre contas")
public class TransferenciaController {

    private final RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    public TransferenciaController(RealizarTransferenciaUseCase realizarTransferenciaUseCase) {
        this.realizarTransferenciaUseCase = realizarTransferenciaUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Realizar transferência",
            description = "Transfere um valor entre duas contas. Garante consistência em cenários de alta concorrência via lock pessimista no PostgreSQL."
    )
    @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou mesma conta de origem e destino")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    public TransferenciaResponse transferir(@Valid @RequestBody TransferenciaRequest request) {
        return TransferenciaResponse.from(
                realizarTransferenciaUseCase.transferir(
                        request.contaOrigemId(),
                        request.contaDestinoId(),
                        request.valor()
                )
        );
    }
}
