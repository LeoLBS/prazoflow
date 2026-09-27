package br.com.leperber.prazoflow.entity;

public record ResultadoNotificacao(boolean enviada, String detalhe) {

    public static ResultadoNotificacao enviada(String detalhe) {
        return new ResultadoNotificacao(true, detalhe);
    }

    public static ResultadoNotificacao naoEnviada(String detalhe) {
        return new ResultadoNotificacao(false, detalhe);
    }
}
