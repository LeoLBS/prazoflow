package br.com.leperber.prazoflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "O nome de usuário não pode ser vazio ou nulo!") String usuario,
        @NotBlank(message = "A senha não pode ser vazia ou nula!")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres!") String senha
) {
}