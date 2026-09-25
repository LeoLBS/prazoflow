package br.com.leperber.prazoflow.web.dto;

import java.time.LocalDate;

public record DemandaFormDTO(
        String titulo,
        String descricao,
        LocalDate dataVencimento,
        String observacao,
        Long tecnicoId
) {
}