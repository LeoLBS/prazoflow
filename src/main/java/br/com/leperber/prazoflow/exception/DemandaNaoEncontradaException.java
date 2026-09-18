package br.com.leperber.prazoflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DemandaNaoEncontradaException extends RuntimeException {

    public DemandaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}