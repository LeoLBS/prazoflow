package br.com.leperber.prazoflow.controller;

import br.com.leperber.prazoflow.dto.TecnicoRequestDTO;
import br.com.leperber.prazoflow.dto.TecnicoResponseDTO;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.service.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<TecnicoResponseDTO> criar(@Valid @RequestBody TecnicoRequestDTO dto) {
        Tecnico tecnico = new Tecnico(dto.nome());
        tecnico.setEmail(dto.email());
        tecnico.setCodigoIdDiscord(dto.codigoIdDiscord());

        Tecnico salvo = tecnicoService.criar(tecnico);

        return ResponseEntity.status(HttpStatus.CREATED).body(TecnicoResponseDTO.from(salvo));
    }

    @GetMapping("/{id}")
    public TecnicoResponseDTO buscarId(@PathVariable Long id) {
        return TecnicoResponseDTO.from(tecnicoService.buscarId(id));
    }

    @GetMapping
    public Page<TecnicoResponseDTO> buscarTecnicos(Pageable pageable) {
        return tecnicoService.buscarTecnicos(pageable).map(TecnicoResponseDTO::from);
    }

    @GetMapping("/buscar")
    public List<TecnicoResponseDTO> buscarPorNome(@RequestParam String nome) {
        return tecnicoService.buscarTecnicosNome(nome).stream()
                .map(TecnicoResponseDTO::from)
                .toList();
    }

    @GetMapping("/discord/{codigoIdDiscord}")
    public TecnicoResponseDTO buscarPorDiscord(@PathVariable String codigoIdDiscord) {
        return TecnicoResponseDTO.from(tecnicoService.buscarIdDiscord(codigoIdDiscord));
    }

    @PatchMapping("/{id}/nome")
    public TecnicoResponseDTO alterarNome(@PathVariable Long id, @RequestParam String nome) {
        return TecnicoResponseDTO.from(tecnicoService.alterarNome(id, nome));
    }

    @PatchMapping("/{id}/email")
    public TecnicoResponseDTO alterarEmail(@PathVariable Long id, @RequestParam String email) {
        return TecnicoResponseDTO.from(tecnicoService.alterarEmail(id, email));
    }

    @PatchMapping("/{id}/discord")
    public TecnicoResponseDTO alterarIdDiscord(@PathVariable Long id, @RequestParam String codigoIdDiscord) {
        return TecnicoResponseDTO.from(tecnicoService.alterarIdDiscord(id, codigoIdDiscord));
    }

    @PatchMapping("/{id}/status")
    public TecnicoResponseDTO alterarStatus(@PathVariable Long id, @RequestParam StatusPadrao status) {
        return TecnicoResponseDTO.from(tecnicoService.alterarStatus(id, status));
    }
}