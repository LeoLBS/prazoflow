package br.com.leperber.prazoflow.dto;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String usuario,
        StatusPadrao status
) {
    public static UsuarioResponseDTO from(Usuario entidade) {
        return new UsuarioResponseDTO(
                entidade.getId(),
                entidade.getUsuario(),
                entidade.getStatus()
        );
    }
}