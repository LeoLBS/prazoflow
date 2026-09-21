package br.com.leperber.prazoflow.controller;

import br.com.leperber.prazoflow.dto.AlterarSenhaDTO;
import br.com.leperber.prazoflow.dto.UsuarioRequestDTO;
import br.com.leperber.prazoflow.dto.UsuarioResponseDTO;
import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Usuario;
import br.com.leperber.prazoflow.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario(dto.usuario(), dto.senha());

        Usuario salvo = usuarioService.criar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.from(salvo));
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarId(@PathVariable Long id) {
        return UsuarioResponseDTO.from(usuarioService.buscarId(id));
    }

    @PatchMapping("/{id}/senha")
    public UsuarioResponseDTO alterarSenha(@PathVariable Long id, @Valid @RequestBody AlterarSenhaDTO dto) {
        return UsuarioResponseDTO.from(usuarioService.alterarSenha(id, dto.senhaAtual(), dto.novaSenha()));
    }

    @PatchMapping("/{id}/status")
    public UsuarioResponseDTO alterarStatus(@PathVariable Long id, @RequestParam StatusPadrao status) {
        return UsuarioResponseDTO.from(usuarioService.alterarStatus(id, status));
    }
}