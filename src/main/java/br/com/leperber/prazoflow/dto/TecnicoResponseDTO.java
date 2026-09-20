package br.com.leperber.prazoflow.dto;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;

public record TecnicoResponseDTO(
        Long id,
        String nome,
        String email,
        String codigoIdDiscord,
        StatusPadrao status
) {
    public static TecnicoResponseDTO from(Tecnico tecnico) {
        return new TecnicoResponseDTO(
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getEmail(),
                tecnico.getCodigoIdDiscord(),
                tecnico.getStatus()
        );
    }
}