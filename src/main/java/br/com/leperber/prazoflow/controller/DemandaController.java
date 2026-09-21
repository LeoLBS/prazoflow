package br.com.leperber.prazoflow.controller;

import br.com.leperber.prazoflow.dto.DemandaRequestDTO;
import br.com.leperber.prazoflow.dto.DemandaResponseDTO;
import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;
import br.com.leperber.prazoflow.service.DemandaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/demandas")
public class DemandaController {

    private final DemandaService demandaService;

    public DemandaController(DemandaService demandaService) {
        this.demandaService = demandaService;
    }

    @PostMapping
    public ResponseEntity<DemandaResponseDTO> criar(@Valid @RequestBody DemandaRequestDTO dto) {
        Demanda demanda = new Demanda(dto.titulo(), dto.descricao(), dto.dataVencimento(), dto.observacao());

        Demanda salva = demandaService.criar(demanda);

        return ResponseEntity.status(HttpStatus.CREATED).body(DemandaResponseDTO.from(salva));
    }

    @GetMapping("/{id}")
    public DemandaResponseDTO buscarId(@PathVariable Long id) {
        return DemandaResponseDTO.from(demandaService.buscarId(id));
    }

    @GetMapping
    public Page<DemandaResponseDTO> buscarDemandas(Pageable pageable) {
        return demandaService.buscarDemandas(pageable).map(DemandaResponseDTO::from);
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<DemandaResponseDTO> buscarPorTecnico(@PathVariable Long tecnicoId) {
        return demandaService.buscarPorTecnico(tecnicoId).stream()
                .map(DemandaResponseDTO::from)
                .toList();
    }

    @PatchMapping("/{id}/titulo")
    public DemandaResponseDTO alterarTitulo(@PathVariable Long id, @RequestParam String titulo) {
        return DemandaResponseDTO.from(demandaService.alterarTitulo(id, titulo));
    }

    @PatchMapping("/{id}/descricao")
    public DemandaResponseDTO alterarDescricao(@PathVariable Long id, @RequestParam String descricao) {
        return DemandaResponseDTO.from(demandaService.alterarDescricao(id, descricao));
    }

    @PatchMapping("/{id}/observacao")
    public DemandaResponseDTO alterarObservacao(@PathVariable Long id, @RequestParam String observacao) {
        return DemandaResponseDTO.from(demandaService.alterarObservacao(id, observacao));
    }

    @PatchMapping("/{id}/reagendar")
    public DemandaResponseDTO reagendarPrazo(@PathVariable Long id, @RequestParam LocalDate novaDataVencimento) {
        return DemandaResponseDTO.from(demandaService.reagendarPrazo(id, novaDataVencimento));
    }

    @PatchMapping("/{id}/tecnico/{tecnicoId}")
    public DemandaResponseDTO atribuirTecnico(@PathVariable Long id, @PathVariable Long tecnicoId) {
        return DemandaResponseDTO.from(demandaService.atribuirTecnico(id, tecnicoId));
    }

    @PatchMapping("/{id}/status")
    public DemandaResponseDTO alterarStatus(@PathVariable Long id, @RequestParam StatusDemanda status) {
        return DemandaResponseDTO.from(demandaService.alterarStatus(id, status));
    }
}