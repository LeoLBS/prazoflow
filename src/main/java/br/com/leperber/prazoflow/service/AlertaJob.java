package br.com.leperber.prazoflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertaJob {

    private static final Logger log = LoggerFactory.getLogger(AlertaJob.class);

    private final AlertaService alertaService;

    public AlertaJob(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @Scheduled(cron = "${prazoflow.alertas.cron:0 0 8 * * *}", zone = "America/Sao_Paulo")
    public void executarNoHorario() {
        executar("agendado");
    }

    // Recupera alertas que ficaram pendentes se o app estava desligado às 08:00.
    @EventListener(ApplicationReadyEvent.class)
    public void executarNaSubida() {
        executar("subida");
    }

    private synchronized void executar(String origem) {
        log.info("Verificacao de alertas iniciada ({})", origem);
        try {
            alertaService.processarAlertas();
        } catch (RuntimeException e) {
            log.error("Falha na verificacao de alertas", e);
        }
        log.info("Verificacao de alertas concluida");
    }
}