package org.cordeirops.compassbank.config;

import jakarta.servlet.http.HttpServletRequest;
import org.cordeirops.compassbank.adapter.in.web.dto.ErroResponse;
import org.cordeirops.compassbank.domain.exception.ContaNaoEncontradaException;
import org.cordeirops.compassbank.domain.exception.MesmaContaTransferenciaException;
import org.cordeirops.compassbank.domain.exception.SaldoInsuficienteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ContaNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse handleContaNaoEncontrada(ContaNaoEncontradaException ex,
                                                  HttpServletRequest request) {
        return new ErroResponse(404, "Não encontrado", ex.getMessage(),
                LocalDateTime.now(), request.getRequestURI());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResponse handleSaldoInsuficiente(SaldoInsuficienteException ex,
                                                 HttpServletRequest request) {
        return new ErroResponse(422, "Entidade não processável", ex.getMessage(),
                LocalDateTime.now(), request.getRequestURI());
    }

    @ExceptionHandler(MesmaContaTransferenciaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleMesmaConta(MesmaContaTransferenciaException ex,
                                          HttpServletRequest request) {
        return new ErroResponse(400, "Requisição inválida", ex.getMessage(),
                LocalDateTime.now(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleValidacao(MethodArgumentNotValidException ex,
                                         HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new ErroResponse(400, "Requisição inválida", mensagem,
                LocalDateTime.now(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResponse handleErroGenerico(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado: {}", ex.getMessage(), ex);
        return new ErroResponse(500, "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente.",
                LocalDateTime.now(), request.getRequestURI());
    }
}
