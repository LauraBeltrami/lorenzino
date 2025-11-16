package org.example.ids.DTO;


import org.example.ids.Model.Evento;

import java.util.List;

public final class EventoMapper {
    private EventoMapper() {}

    public static EventoDTO toDTO(Evento e) {
        var inviti = e.getInviti().stream().map(i ->
                new InvitoDTO(
                        i.getVenditore().getId(),
                        i.getVenditore().getNome(),
                        i.getNota()
                )
        ).toList();

        return new EventoDTO(
                e.getId(), e.getTitolo(), e.getDescrizione(), e.getLuogo(),
                e.getInizio(), e.getFine(),
                e.getAnimatore().getId(), e.getAnimatore().getNome(),
                inviti
        );
    }

    public static List<EventoDTO> toDTO(List<Evento> eventi) {
        return eventi.stream().map(EventoMapper::toDTO).toList();
    }
}