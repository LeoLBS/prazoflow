package br.com.leperber.prazoflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TecnicoRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio ou nulo!") String nome,
        @Email(message = "O email informado não é válido!") String email,
        String codigoIdDiscord
) {
}