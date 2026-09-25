package br.com.leperber.prazoflow.web;

import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.service.TecnicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            return "tecnicos/form";
        }
    }
}