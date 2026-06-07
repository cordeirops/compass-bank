package org.cordeirops.compassbank.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.cordeirops.compassbank.adapter.in.web.dto.ContaResponseDTO;
import org.cordeirops.compassbank.adapter.in.web.dto.CriarContaRequestDTO;
import org.cordeirops.compassbank.adapter.in.web.dto.TransacaoResponseDTO;
import org.cordeirops.compassbank.application.port.in.ConsultarContaUseCase;
import org.cordeirops.compassbank.application.port.in.CriarContaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contas")
@Tag(name = "Contas", description = "Gestão de contas bancárias")
public class ContaController {

    private final CriarContaUseCase criarContaUseCase;
    private final ConsultarContaUseCase consultarContaUseCase;

    public ContaController(CriarContaUseCase criarContaUseCase,
                           ConsultarContaUseCase consultarContaUseCase) {
        this.criarContaUseCase = criarContaUseCase;
        this.consultarContaUseCase = consultarContaUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar contas", description = "Retorna todas as contas bancárias")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public List<ContaResponseDTO> listar() {
        return consultarContaUseCase.listarContas().stream()
                .map(ContaResponseDTO::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar conta", description = "Cria uma nova conta bancária com saldo inicial")
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ContaResponseDTO criar(@Valid @RequestBody CriarContaRequestDTO request) {
        return ContaResponseDTO.from(
                criarContaUseCase.criarConta(request.nome(), request.saldoInicial())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar conta", description = "Retorna os dados e saldo atual de uma conta")
    @ApiResponse(responseCode = "200", description = "Conta encontrada")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    public ContaResponseDTO buscar(@PathVariable UUID id) {
        return ContaResponseDTO.from(consultarContaUseCase.buscarConta(id));
    }

    @GetMapping("/{id}/transacoes")
    @Operation(summary = "Histórico de transações", description = "Retorna o histórico de movimentações da conta")
    @ApiResponse(responseCode = "200", description = "Histórico retornado")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    public List<TransacaoResponseDTO> historico(@PathVariable UUID id) {
        return consultarContaUseCase.buscarHistorico(id)
                .stream()
                .map(TransacaoResponseDTO::from)
                .toList();
    }
}
