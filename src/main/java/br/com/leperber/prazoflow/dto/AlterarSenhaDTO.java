package br.com.leperber.prazoflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(
        @NotBlank(message = "A senha atual não pode ser vazia ou nula!") String senhaAtual,
        @NotBlank(message = "A nova senha não pode ser vazia ou nula!")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres!") String novaSenha
) {
}