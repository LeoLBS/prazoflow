package br.com.leperber.prazoflow.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DemandaRequestDTO(
        @NotBlank(message = "O título não pode ser vazio ou nulo!") String titulo,
        @NotBlank(message = "A descrição não pode ser vazia ou nula!") String descricao,
        @NotNull(message = "A data de vencimento não pode ser nula!")
        @Future(message = "A data de vencimento deve estar no futuro!") LocalDate dataVencimento,
        String observacao
) {
}