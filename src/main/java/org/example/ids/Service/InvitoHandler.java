package org.example.ids.Service;

import org.example.ids.DTO.InvitoDTO;
import org.example.ids.Model.StatoInvito;

public interface InvitoHandler {
    public InvitoDTO rispondiInvito(Long eventoId, Long venditoreId, StatoInvito stato);
}
