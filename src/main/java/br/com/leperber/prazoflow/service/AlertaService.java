package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.bot.DiscordNotificador;
import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.repository.DemandaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final int DIAS_ANTECEDENCIA = 2;
    private static final int LIMITE_FIELDS_POR_EMBED = 25; // limite do Discord por embed
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DemandaRepository demandaRepository;
    private final DiscordNotificador notificador;

    public AlertaService(DemandaRepository demandaRepository, DiscordNotificador notificador) {
        this.demandaRepository = demandaRepository;
        this.notificador = notificador;
    }

    public void processarAlertas() {
        marcarAtrasadas();
        enviarAlertasDePrazo();
        enviarAlertasDeAtraso();
    }

    // Atraso é fato de negócio: vira ATRASADO independentemente do Discord estar no ar.
    private void marcarAtrasadas() {
        List<Demanda> vencidas = demandaRepository
                .findByStatusAndDataVencimentoBefore(StatusDemanda.PENDENTE, LocalDate.now(FUSO));
        if (vencidas.isEmpty()) {
            return;
        }
        vencidas.forEach(d -> d.setStatus(StatusDemanda.ATRASADO));
        demandaRepository.saveAll(vencidas);
        log.info("{} demanda(s) marcada(s) como ATRASADO", vencidas.size());
    }

    private void enviarAlertasDePrazo() {
        LocalDate hoje = LocalDate.now(FUSO);
        List<Demanda> demandas = demandaRepository
                .findByStatusAndAlertaPrazoEnviadoFalseAndDataVencimentoBetween(
                        StatusDemanda.PENDENTE, hoje, hoje.plusDays(DIAS_ANTECEDENCIA));
        enviarPorTecnico(demandas, TipoAlerta.PRAZO);
    }

    private void enviarAlertasDeAtraso() {
        List<Demanda> demandas = demandaRepository
                .findByStatusAndAlertaAtrasoEnviadoFalse(StatusDemanda.ATRASADO);
        enviarPorTecnico(demandas, TipoAlerta.ATRASO);
    }

    private void enviarPorTecnico(List<Demanda> demandas, TipoAlerta tipo) {
        long semTecnico = demandas.stream().filter(d -> d.getTecnico() == null).count();
        if (semTecnico > 0) {
            log.warn("{} demanda(s) sem tecnico atribuido: alerta de {} nao enviado", semTecnico, tipo);
        }

        Map<Tecnico, List<Demanda>> porTecnico = demandas.stream()
                .filter(d -> d.getTecnico() != null)
                .collect(Collectors.groupingBy(Demanda::getTecnico));

        porTecnico.forEach((tecnico, lista) -> {
            if (tecnico.getStatus() != StatusPadrao.ATIVO
                    || !StringUtils.hasText(tecnico.getCodigoIdDiscord())) {
                log.warn("Tecnico {} inativo ou sem Discord ID: alerta de {} nao enviado", tecnico.getId(), tipo);
                return;
            }
            try {
                enviarParaTecnico(tecnico, lista, tipo);
            } catch (CompletionException e) {
                Throwable causa = e.getCause() != null ? e.getCause() : e;
                log.warn("Nao foi possivel enviar alerta de {} ao tecnico {}: {}",
                        tipo, tecnico.getId(), causa.getMessage());
            } catch (RuntimeException e) {
                log.error("Erro inesperado no alerta de {} do tecnico {}", tipo, tecnico.getId(), e);
            }
        });
    }

    // Flag só é gravado DEPOIS da DM sair; se falhar, o próximo ciclo tenta de novo.
    private void enviarParaTecnico(Tecnico tecnico, List<Demanda> demandas, TipoAlerta tipo) {
        for (List<Demanda> lote : dividirEmLotes(demandas)) {
            MessageEmbed embed = montarEmbed(tecnico, lote, tipo);
            notificador.enviarDm(tecnico.getCodigoIdDiscord(), embed).join();
            lote.forEach(tipo::marcarEnviado);
            demandaRepository.saveAll(lote);
        }
    }

    private List<List<Demanda>> dividirEmLotes(List<Demanda> demandas) {
        List<List<Demanda>> lotes = new ArrayList<>();
        for (int i = 0; i < demandas.size(); i += LIMITE_FIELDS_POR_EMBED) {
            lotes.add(demandas.subList(i, Math.min(i + LIMITE_FIELDS_POR_EMBED, demandas.size())));
        }
        return lotes;
    }

    private MessageEmbed montarEmbed(Tecnico tecnico, List<Demanda> lote, TipoAlerta tipo) {
        EmbedBuilder builder = tipo.novoEmbed()
                .setDescription("Olá, **" + tecnico.getNome() + "**! " + tipo.mensagemIntroducao());

        for (Demanda demanda : lote) {
            builder.addField(demanda.getTitulo(), formatarValorDoField(demanda, tipo), false);
        }

        return builder.build();
    }

    private String formatarValorDoField(Demanda demanda, TipoAlerta tipo) {
        String prazo = "Prazo: " + demanda.getDataVencimento().format(FORMATO_DATA);
        if (tipo == TipoAlerta.PRAZO) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(FUSO), demanda.getDataVencimento());
            prazo += " (" + descreverDiasRestantes(diasRestantes) + ")";
        }
        return prazo;
    }

    private String descreverDiasRestantes(long dias) {
        if (dias == 0) {
            return "vence hoje";
        }
        if (dias == 1) {
            return "falta 1 dia";
        }
        return "faltam " + dias + " dias";
    }

    private enum TipoAlerta {
        PRAZO {
            @Override
            EmbedBuilder novoEmbed() {
                return DiscordNotificador.novoEmbedDePrazo();
            }

            @Override
            String mensagemIntroducao() {
                return "Estas demandas vencem nos próximos dias:";
            }

            @Override
            void marcarEnviado(Demanda demanda) {
                demanda.marcarAlertaPrazoEnviado();
            }
        },
        ATRASO {
            @Override
            EmbedBuilder novoEmbed() {
                return DiscordNotificador.novoEmbedDeAtraso();
            }

            @Override
            String mensagemIntroducao() {
                return "Estas demandas passaram do prazo e ainda não foram concluídas:";
            }

            @Override
            void marcarEnviado(Demanda demanda) {
                demanda.marcarAlertaAtrasoEnviado();
            }
        };

        abstract EmbedBuilder novoEmbed();

        abstract String mensagemIntroducao();

        abstract void marcarEnviado(Demanda demanda);
    }
}