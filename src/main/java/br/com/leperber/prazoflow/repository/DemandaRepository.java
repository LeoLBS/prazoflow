package br.com.leperber.prazoflow.repository;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DemandaRepository extends JpaRepository<Demanda, Long> {
    List<Demanda> findByDataVencimentoAndStatus(LocalDate dataVencimento, StatusDemanda status);
    List<Demanda> findByStatusAndDataVencimentoBefore(StatusDemanda status, LocalDate dataVencimento);
    List<Demanda> findByTecnico_Id(Long tecnicoId);
    List<Demanda> findByStatusAndAlertaPrazoEnviadoFalseAndDataVencimentoBetween(
            StatusDemanda status, LocalDate inicio, LocalDate fim);
    List<Demanda> findByStatusAndAlertaAtrasoEnviadoFalse(StatusDemanda status);
}