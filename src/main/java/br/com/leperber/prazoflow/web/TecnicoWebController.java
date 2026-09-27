package br.com.leperber.prazoflow.web;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.service.TecnicoService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/painel/tecnicos")
public class TecnicoWebController {

    private final TecnicoService tecnicoService;

    public TecnicoWebController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public String listar(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<Tecnico> pagina = tecnicoService.buscarTecnicos(pageable);
        model.addAttribute("pagina", pagina);
        return "tecnicos/lista";
    }

    @GetMapping("/novo")
    public String novoFormulario(Model model) {
        model.addAttribute("tecnico", new Tecnico());
        model.addAttribute("idEdicao", null);
        return "tecnicos/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Tecnico tecnico, Model model) {
        try {
            tecnicoService.criar(tecnico);
            return "redirect:/painel/tecnicos";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tecnico", tecnico);
            model.addAttribute("idEdicao", null);
            return "tecnicos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("tecnico", tecnicoService.buscarId(id));
        model.addAttribute("idEdicao", id);
        return "tecnicos/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @ModelAttribute Tecnico tecnico, Model model) {
        try {
            tecnicoService.atualizar(id, tecnico.getNome(), tecnico.getEmail(), tecnico.getCodigoIdDiscord());
            return "redirect:/painel/tecnicos";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tecnico", tecnico);
            model.addAttribute("idEdicao", id);
            return "tecnicos/form";
        }
    }

    @PostMapping("/{id}/status")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Tecnico tecnico = tecnicoService.buscarId(id);
            StatusPadrao novoStatus = tecnico.getStatus() == StatusPadrao.ATIVO
                    ? StatusPadrao.INATIVO
                    : StatusPadrao.ATIVO;
            tecnicoService.alterarStatus(id, novoStatus);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/painel/tecnicos";
    }
}