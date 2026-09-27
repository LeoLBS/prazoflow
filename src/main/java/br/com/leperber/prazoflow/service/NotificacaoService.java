package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.bot.DiscordNotificador;
import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.ResultadoNotificacao;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final long TIMEOUT_SEGUNDOS = 5;

    private final DiscordNotificador notificador;

    public NotificacaoService(DiscordNotificador notificador) {
        this.notificador = notificador;
    }

    public ResultadoNotificacao notificarReagendamento(Demanda demanda, LocalDate dataAnterior) {
        return enviarAoResponsavel(demanda.getTecnico(), demanda, "reagendamento", nome ->
                DiscordNotificador.novoEmbedDeReagendamento()
                        .setDescription("Olá, **%s**! O prazo da demanda **%s** foi reagendado."
                                .formatted(nome, demanda.getTitulo()))
                        .addField("Prazo anterior", dataAnterior.format(FORMATO_DATA), true)
                        .addField("Novo prazo", demanda.getDataVencimento().format(FORMATO_DATA), true)
                        .build());
    }

    public ResultadoNotificacao notificarConclusao(Demanda demanda) {
        return enviarAoResponsavel(demanda.getTecnico(), demanda, "conclusao", nome ->
                DiscordNotificador.novoEmbedDeConclusao()
                        .setDescription("Parabéns, **%s**! A demanda **%s** foi concluída. Obrigado pela entrega! 🎉"
                                .formatted(nome, demanda.getTitulo()))
                        .build());
    }

    public ResultadoNotificacao notificarVinculo(Demanda demanda) {
        return enviarAoResponsavel(demanda.getTecnico(), demanda, "vinculo", nome -> {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), demanda.getDataVencimento());

            return DiscordNotificador.novoEmbedDeVinculo()
                    .setDescription("Olá, **%s**! A demanda **%s** foi vinculada a você."
                            .formatted(nome, demanda.getTitulo()))
                    .addField("Prazo", demanda.getDataVencimento().format(FORMATO_DATA), true)
                    .addField("Dias restantes", formatarDiasRestantes(diasRestantes), true)
                    .build();
        });
    }

    public ResultadoNotificacao notificarDesvinculo(Tecnico tecnicoAnterior, Demanda demanda) {
        return enviarAoResponsavel(tecnicoAnterior, demanda, "desvinculo", nome ->
                DiscordNotificador.novoEmbedDeDesvinculo()
                        .setDescription("Olá, **%s**! A demanda **%s** foi removida de você e não é mais de sua responsabilidade."
                                .formatted(nome, demanda.getTitulo()))
                        .build());
    }

    private String formatarDiasRestantes(long diasRestantes) {
        if (diasRestantes < 0) {
            return "atrasado há " + Math.abs(diasRestantes) + (Math.abs(diasRestantes) == 1 ? " dia" : " dias");
        }
        if (diasRestantes == 0) {
            return "vence hoje";
        }
        return diasRestantes == 1 ? "falta 1 dia" : "faltam " + diasRestantes + " dias";
    }

    private ResultadoNotificacao enviarAoResponsavel(Tecnico tecnico, Demanda demanda, String tipo,
                                                       Function<String, MessageEmbed> montarEmbed) {
        if (tecnico == null) {
            return ResultadoNotificacao.naoEnviada("Demanda sem técnico responsável.");
        }
        if (tecnico.getStatus() != StatusPadrao.ATIVO) {
            return ResultadoNotificacao.naoEnviada("O técnico " + tecnico.getNome() + " está inativo.");
        }
        if (!StringUtils.hasText(tecnico.getCodigoIdDiscord())) {
            return ResultadoNotificacao.naoEnviada("O técnico " + tecnico.getNome() + " não possui ID do Discord cadastrado.");
        }

        try {
            notificador.enviarDm(tecnico.getCodigoIdDiscord(), montarEmbed.apply(tecnico.getNome()))
                    .get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
            return ResultadoNotificacao.enviada("Notificação enviada a " + tecnico.getNome() + " pelo Discord.");
        } catch (TimeoutException e) {
            log.warn("Tempo esgotado ao enviar notificacao de {} ao tecnico {}", tipo, tecnico.getId());
            return ResultadoNotificacao.naoEnviada("Tempo esgotado ao tentar notificar " + tecnico.getNome()
                    + " pelo Discord.");
        } catch (ExecutionException e) {
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            log.warn("Nao foi possivel enviar notificacao de {} ao tecnico {}: {}", tipo, tecnico.getId(), causa.getMessage());
            return ResultadoNotificacao.naoEnviada("Não foi possível notificar " + tecnico.getNome()
                    + " pelo Discord (verifique se ele está no servidor e com DMs habilitadas).");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResultadoNotificacao.naoEnviada("Envio da notificação foi interrompido.");
        } catch (RuntimeException e) {
            log.error("Erro inesperado na notificacao de {} do tecnico {}", tipo, tecnico.getId(), e);
            return ResultadoNotificacao.naoEnviada("Erro inesperado ao notificar " + tecnico.getNome() + ".");
        }
    }
}
