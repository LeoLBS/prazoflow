package br.com.leperber.prazoflow.exception;

public class PrazoInvalidoException extends IllegalArgumentException {

    public PrazoInvalidoException(String mensagem) {
        super(mensagem);
    }
}