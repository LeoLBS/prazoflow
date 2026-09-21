package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.bot.DiscordNotificador;
import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DiscordNotificador notificador;

    public NotificacaoService(DiscordNotificador notificador) {
        this.notificador = notificador;
    }

    public void notificarReagendamento(Demanda demanda, LocalDate dataAnterior) {
        enviarAoResponsavel(demanda, "reagendamento", nome ->
                "Olá, %s! O prazo da demanda **%s** foi reagendado de %s para %s."
                        .formatted(nome, demanda.getTitulo(),
                                dataAnterior.format(FORMATO_DATA),
                                demanda.getDataVencimento().format(FORMATO_DATA)));
    }

    public void notificarConclusao(Demanda demanda) {
        enviarAoResponsavel(demanda, "conclusao", nome ->
                "Parabéns, %s! A demanda **%s** foi concluída. Obrigado pela entrega!"
                        .formatted(nome, demanda.getTitulo()));
    }

    private void enviarAoResponsavel(Demanda demanda, String tipo, Function<String, String> montarMensagem) {
        Tecnico tecnico = demanda.getTecnico();
        if (tecnico == null
                || tecnico.getStatus() != StatusPadrao.ATIVO
                || !StringUtils.hasText(tecnico.getCodigoIdDiscord())) {
            log.info("Demanda {} sem responsavel apto a receber DM: notificacao de {} ignorada",
                    demanda.getId(), tipo);
            return;
        }

        try {
            notificador.enviarDm(tecnico.getCodigoIdDiscord(), montarMensagem.apply(tecnico.getNome()))
                    .exceptionally(ex -> {
                        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                        log.warn("Nao foi possivel enviar notificacao de {} ao tecnico {}: {}",
                                tipo, tecnico.getId(), causa.getMessage());
                        return null;
                    });
        } catch (RuntimeException e) {
            log.error("Erro inesperado na notificacao de {} do tecnico {}", tipo, tecnico.getId(), e);
        }
    }
}