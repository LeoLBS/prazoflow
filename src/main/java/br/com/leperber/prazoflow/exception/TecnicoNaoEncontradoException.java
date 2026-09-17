package br.com.leperber.prazoflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TecnicoNaoEncontradoException extends RuntimeException {

    public TecnicoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}