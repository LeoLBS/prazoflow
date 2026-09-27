package br.com.leperber.prazoflow.web;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.ResultadoNotificacao;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.service.DemandaService;
import br.com.leperber.prazoflow.service.NotificacaoService;
import br.com.leperber.prazoflow.service.TecnicoService;
import br.com.leperber.prazoflow.web.dto.DemandaFormDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/painel/demandas")
public class DemandaWebController {

    private final DemandaService demandaService;
    private final TecnicoService tecnicoService;
    private final NotificacaoService notificacaoService;

    public DemandaWebController(DemandaService demandaService, TecnicoService tecnicoService,
                                 NotificacaoService notificacaoService) {
        this.demandaService = demandaService;
        this.tecnicoService = tecnicoService;
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public String listar(@PageableDefault(size = 20) Pageable pageable, Model model,
                          @RequestParam(required = false) StatusDemanda status) {
        Page<Demanda> pagina = demandaService.buscarDemandas(status, pageable);
        model.addAttribute("pagina", pagina);
        model.addAttribute("statusSelecionado", status);
        return "demandas/lista";
    }

    @GetMapping("/nova")
    public String novoFormulario(Model model) {
        model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
        model.addAttribute("idEdicao", null);
        return "demandas/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute DemandaFormDTO formulario, Model model, RedirectAttributes redirectAttributes) {
        try {
            Demanda demanda = new Demanda();
            demanda.setTitulo(formulario.titulo());
            demanda.setDescricao(formulario.descricao());
            demanda.setDataVencimento(formulario.dataVencimento());
            demanda.setObservacao(formulario.observacao());

            if (formulario.tecnicoId() != null) {
                Tecnico tecnico = tecnicoService.buscarId(formulario.tecnicoId());
                demanda.setTecnico(tecnico);
            }

            Demanda salva = demandaService.criar(demanda);

            adicionarFlashDeConclusao("Demanda criada com sucesso.", salva.getResultadoUltimaNotificacao(), redirectAttributes);

            return "redirect:/painel/demandas";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
            model.addAttribute("formulario", formulario);
            model.addAttribute("idEdicao", null);
            return "demandas/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Demanda demanda = demandaService.buscarId(id);

        DemandaFormDTO formulario = new DemandaFormDTO(
                demanda.getTitulo(),
                demanda.getDescricao(),
                demanda.getDataVencimento(),
                demanda.getObservacao(),
                demanda.getTecnico() != null ? demanda.getTecnico().getId() : null
        );

        model.addAttribute("formulario", formulario);
        model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
        model.addAttribute("idEdicao", id);
        return "demandas/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @ModelAttribute DemandaFormDTO formulario, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Demanda demanda = demandaService.buscarId(id);

            if (!Objects.equals(formulario.titulo(), demanda.getTitulo())) {
                demandaService.alterarTitulo(id, formulario.titulo());
            }
            if (!Objects.equals(formulario.descricao(), demanda.getDescricao())) {
                demandaService.alterarDescricao(id, formulario.descricao());
            }
            if (!Objects.equals(formulario.observacao(), demanda.getObservacao())) {
                demandaService.alterarObservacao(id, formulario.observacao());
            }
            if (!Objects.equals(formulario.dataVencimento(), demanda.getDataVencimento())) {
                demandaService.reagendarPrazo(id, formulario.dataVencimento());
            }

            Long tecnicoAtualId = demanda.getTecnico() != null ? demanda.getTecnico().getId() : null;
            ResultadoNotificacao resultadoNotificacao = null;
            if (formulario.tecnicoId() != null && !Objects.equals(formulario.tecnicoId(), tecnicoAtualId)) {
                Demanda salva = demandaService.atribuirTecnico(id, formulario.tecnicoId());
                resultadoNotificacao = salva.getResultadoUltimaNotificacao();
            }

            adicionarFlashDeConclusao("Demanda atualizada com sucesso.", resultadoNotificacao, redirectAttributes);

            return "redirect:/painel/demandas";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
            model.addAttribute("formulario", formulario);
            model.addAttribute("idEdicao", id);
            return "demandas/form";
        }
    }

    @PostMapping("/{id}/concluir")
    public String concluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandaService.alterarStatus(id, StatusDemanda.CONCLUIDO);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/painel/demandas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandaService.alterarStatus(id, StatusDemanda.CANCELADO);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/painel/demandas";
    }

    @PostMapping("/{id}/reenviar-notificacao")
    public String reenviarNotificacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Demanda demanda = demandaService.buscarId(id);

        ResultadoNotificacao resultado = switch (demanda.getStatus()) {
            case CONCLUIDO -> notificacaoService.notificarConclusao(demanda);
            case CANCELADO -> notificacaoService.notificarCancelamento(demanda);
            case PENDENTE, ATRASADO -> notificacaoService.notificarVinculo(demanda);
        };

        if (resultado.enviada()) {
            redirectAttributes.addFlashAttribute("sucesso", resultado.detalhe());
        } else {
            redirectAttributes.addFlashAttribute("avisoNotificacao", resultado.detalhe());
        }

        return "redirect:/painel/demandas";
    }

    private void adicionarFlashDeConclusao(String mensagemBase, ResultadoNotificacao resultadoNotificacao,
                                             RedirectAttributes redirectAttributes) {
        if (resultadoNotificacao == null) {
            redirectAttributes.addFlashAttribute("sucesso", mensagemBase);
            return;
        }

        if (resultadoNotificacao.enviada()) {
            redirectAttributes.addFlashAttribute("sucesso", mensagemBase + " " + resultadoNotificacao.detalhe());
        } else {
            redirectAttributes.addFlashAttribute("sucesso", mensagemBase);
            redirectAttributes.addFlashAttribute("avisoNotificacao", resultadoNotificacao.detalhe());
        }
    }
}