package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.exception.DemandaNaoEncontradaException;
import br.com.leperber.prazoflow.exception.TecnicoNaoEncontradoException;
import br.com.leperber.prazoflow.repository.DemandaRepository;
import br.com.leperber.prazoflow.repository.TecnicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class DemandaService {

    private final DemandaRepository demandaRepository;
    private final TecnicoRepository tecnicoRepository;
    private final NotificacaoService notificacaoService;

    public DemandaService(DemandaRepository demandaRepository,
                          TecnicoRepository tecnicoRepository,
                          NotificacaoService notificacaoService) {
        this.demandaRepository = demandaRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.notificacaoService = notificacaoService;
    }

    public Demanda criar(Demanda demanda) {
        if (!StringUtils.hasText(demanda.getTitulo())) {
            throw new IllegalArgumentException("O título não pode ser vazio ou nulo!");
        }
        if (!StringUtils.hasText(demanda.getDescricao())) {
            throw new IllegalArgumentException("A descrição não pode ser vazia ou nula!");
        }
        if (demanda.getDataVencimento() == null) {
            throw new IllegalArgumentException("A data de vencimento não pode ser nula!");
        }
        if (demanda.getDataVencimento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de vencimento não pode estar no passado!");
        }

        demanda.setStatus(StatusDemanda.PENDENTE);

        return demandaRepository.save(demanda);
    }

    public Demanda buscarId(Long id) {
        return demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));
    }

    public Page<Demanda> buscarDemandas(Pageable pageable) {
        return demandaRepository.findAll(pageable);
    }

    public List<Demanda> buscarPorTecnico(Long tecnicoId) {
        return demandaRepository.findByTecnico_Id(tecnicoId);
    }

    public Demanda alterarTitulo(Long id, String titulo) {
        if (!StringUtils.hasText(titulo)) {
            throw new IllegalArgumentException("O título não pode ser vazio ou nulo!");
        }

        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        demanda.setTitulo(titulo);

        return demandaRepository.save(demanda);
    }

    public Demanda alterarDescricao(Long id, String descricao) {
        if (!StringUtils.hasText(descricao)) {
            throw new IllegalArgumentException("A descrição não pode ser vazia ou nula!");
        }

        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        demanda.setDescricao(descricao);

        return demandaRepository.save(demanda);
    }

    public Demanda alterarObservacao(Long id, String observacao) {
        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        demanda.setObservacao(observacao);

        return demandaRepository.save(demanda);
    }

    public Demanda reagendarPrazo(Long id, LocalDate novaDataVencimento) {
        if (novaDataVencimento == null) {
            throw new IllegalArgumentException("A nova data de vencimento não pode ser nula!");
        }
        if (novaDataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A nova data de vencimento não pode estar no passado!");
        }

        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        LocalDate dataAnterior = demanda.getDataVencimento();

        demanda.setDataVencimento(novaDataVencimento);
        demanda.setStatus(StatusDemanda.PENDENTE);
        demanda.reiniciarAlertas();

        Demanda salva = demandaRepository.save(demanda);

        if (!novaDataVencimento.equals(dataAnterior)) {
            notificacaoService.notificarReagendamento(salva, dataAnterior);
        }

        return salva;
    }

    public Demanda atribuirTecnico(Long id, Long tecnicoId) {
        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new TecnicoNaoEncontradoException("Tecnico não encontrado com o ID: " + tecnicoId));

        if (tecnico.getStatus() != StatusPadrao.ATIVO) {
            throw new IllegalArgumentException("Não é possível atribuir uma demanda a um técnico inativo!");
        }

        if (!tecnico.equals(demanda.getTecnico())) {
            demanda.setTecnico(tecnico);
            demanda.reiniciarAlertas();
        }

        return demandaRepository.save(demanda);
    }

    public Demanda alterarStatus(Long id, StatusDemanda statusDemanda) {
        Demanda demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new DemandaNaoEncontradaException("Demanda não encontrada com o ID: " + id));

        if (demanda.getStatus().equals(statusDemanda)) {
            throw new IllegalArgumentException("A demanda já se encontra com o status " + statusDemanda);
        }

        demanda.setStatus(statusDemanda);

        Demanda salva = demandaRepository.save(demanda);

        if (statusDemanda == StatusDemanda.CONCLUIDO) {
            notificacaoService.notificarConclusao(salva);
        }

        return salva;
    }
}