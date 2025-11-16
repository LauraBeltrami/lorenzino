package org.example.ids.DTO;


import org.example.ids.Model.Carrello;

import java.util.List;

public final class CarrelloMapper {
    private CarrelloMapper() {}

    public static CarrelloDTO toDTO(Carrello c) {
        List<RigaProdottoDTO> prodDTOS = c.getItems().stream().map(i ->
                new RigaProdottoDTO(
                        i.getProdotto().getId(),
                        i.getProdotto().getNome(),
                        i.getPrezzoUnitario(),
                        i.getQuantita(),
                        i.getTotaleRiga()
                )
        ).toList();

        List<RigaBundleDTO> bundleDTOS = c.getBundleItems().stream().map(bi ->
                new RigaBundleDTO(
                        bi.getBundle().getId(),
                        bi.getBundle().getNome(),
                        bi.getPrezzoUnitario(),
                        bi.getQuantita(),
                        bi.getTotaleRiga()
                )
        ).toList();

        return new CarrelloDTO(
                c.getId(),
                c.getAcquirente().getId(),
                c.getAcquirente().getNome(),
                prodDTOS,
                bundleDTOS,
                c.getNumeroArticoli(),
                c.getTotale()
        );
    }
}