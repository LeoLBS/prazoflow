package br.com.leperber.prazoflow.web;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.service.DemandaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/painel/demandas")
public class DemandaWebController {

    private final DemandaService demandaService;

    public DemandaWebController(DemandaService demandaService) {
        this.demandaService = demandaService;
    }

    @GetMapping
    public String listar(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<Demanda> pagina = demandaService.buscarDemandas(pageable);
        model.addAttribute("pagina", pagina);
        return "demandas/lista";
    }
}