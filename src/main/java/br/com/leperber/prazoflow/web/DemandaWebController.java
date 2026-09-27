package br.com.leperber.prazoflow.web;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.service.DemandaService;
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

import java.util.Objects;

@Controller
@RequestMapping("/painel/demandas")
public class DemandaWebController {

    private final DemandaService demandaService;
    private final TecnicoService tecnicoService;

    public DemandaWebController(DemandaService demandaService, TecnicoService tecnicoService) {
        this.demandaService = demandaService;
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public String listar(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<Demanda> pagina = demandaService.buscarDemandas(pageable);
        model.addAttribute("pagina", pagina);
        return "demandas/lista";
    }

    @GetMapping("/nova")
    public String novoFormulario(Model model) {
        model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
        model.addAttribute("idEdicao", null);
        return "demandas/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute DemandaFormDTO formulario, Model model) {
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

            demandaService.criar(demanda);

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
    public String atualizar(@PathVariable Long id, @ModelAttribute DemandaFormDTO formulario, Model model) {
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
            if (formulario.tecnicoId() != null && !Objects.equals(formulario.tecnicoId(), tecnicoAtualId)) {
                demandaService.atribuirTecnico(id, formulario.tecnicoId());
            }

            return "redirect:/painel/demandas";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tecnicos", tecnicoService.buscarTecnicos(Pageable.unpaged()).getContent());
            model.addAttribute("formulario", formulario);
            model.addAttribute("idEdicao", id);
            return "demandas/form";
        }
    }
}