package br.com.leperber.prazoflow.dto;

import br.com.leperber.prazoflow.entity.Demanda;
import br.com.leperber.prazoflow.entity.StatusDemanda;

import java.time.LocalDate;

public record DemandaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataVencimento,
        String observacao,
        StatusDemanda status,
        Long tecnicoId,
        String tecnicoNome
) {
    public static DemandaResponseDTO from(Demanda demanda) {
        Long tecnicoId = demanda.getTecnico() != null ? demanda.getTecnico().getId() : null;
        String tecnicoNome = demanda.getTecnico() != null ? demanda.getTecnico().getNome() : null;

        return new DemandaResponseDTO(
                demanda.getId(),
                demanda.getTitulo(),
                demanda.getDescricao(),
                demanda.getDataVencimento(),
                demanda.getObservacao(),
                demanda.getStatus(),
                tecnicoId,
                tecnicoNome
        );
    }
}