package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.bot.DiscordNotificador;
import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.repository.DemandaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private static final int LIMITE_CARACTERES = 1900; // Discord aceita 2000; sobra margem
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
        String cabecalho = tipo.cabecalho(tecnico.getNome());
        for (List<Demanda> lote : dividirEmLotes(demandas, cabecalho)) {
            notificador.enviarDm(tecnico.getCodigoIdDiscord(), montarMensagem(cabecalho, lote)).join();
            lote.forEach(tipo::marcarEnviado);
            demandaRepository.saveAll(lote);
        }
    }

    private List<List<Demanda>> dividirEmLotes(List<Demanda> demandas, String cabecalho) {
        List<List<Demanda>> lotes = new ArrayList<>();
        List<Demanda> atual = new ArrayList<>();
        int tamanho = cabecalho.length();

        for (Demanda demanda : demandas) {
            int tamanhoLinha = formatarLinha(demanda).length() + 1;
            if (!atual.isEmpty() && tamanho + tamanhoLinha > LIMITE_CARACTERES) {
                lotes.add(atual);
                atual = new ArrayList<>();
                tamanho = cabecalho.length();
            }
            atual.add(demanda);
            tamanho += tamanhoLinha;
        }
        if (!atual.isEmpty()) {
            lotes.add(atual);
        }
        return lotes;
    }

    private String montarMensagem(String cabecalho, List<Demanda> lote) {
        String linhas = lote.stream()
                .map(this::formatarLinha)
                .collect(Collectors.joining("\n"));
        return cabecalho + "\n" + linhas;
    }

    private String formatarLinha(Demanda demanda) {
        return "- **" + demanda.getTitulo() + "** (prazo: "
                + demanda.getDataVencimento().format(FORMATO_DATA) + ")";
    }

    private enum TipoAlerta {
        PRAZO("Olá, %s! Estas demandas vencem nos próximos dias:") {
            @Override
            void marcarEnviado(Demanda demanda) {
                demanda.marcarAlertaPrazoEnviado();
            }
        },
        ATRASO("Olá, %s! Estas demandas passaram do prazo e ainda não foram concluídas:") {
            @Override
            void marcarEnviado(Demanda demanda) {
                demanda.marcarAlertaAtrasoEnviado();
            }
        };

        private final String modeloCabecalho;

        TipoAlerta(String modeloCabecalho) {
            this.modeloCabecalho = modeloCabecalho;
        }

        String cabecalho(String nome) {
            return modeloCabecalho.formatted(nome);
        }

        abstract void marcarEnviado(Demanda demanda);
    }
}