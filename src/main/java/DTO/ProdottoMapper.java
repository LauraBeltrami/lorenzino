package DTO;


import Model.Certificazione;
import Model.Prodotto;

import java.util.Collection;
import java.util.List;

public final class ProdottoMapper {
    private ProdottoMapper() {}

    public static ProdottoDTO toDTO(Prodotto p) {
        CertificazioneDTO certDTO = null;
        Certificazione c = p.getCertificazione();
        if (c != null) {
            certDTO = new CertificazioneDTO(
                    c.getId(),
                    c.getDescrizione(),
                    c.getCuratoreValidatore().getId(),
                    c.getCuratoreValidatore().getNome(),
                    c.getDataApprovazione()
            );
        }
        return new ProdottoDTO(
                p.getId(),
                p.getNome(),
                p.getPrezzo(),
                p.getStato().name(),
                p.getVenditore().getId(),
                p.getVenditore().getNome(),
                certDTO
        );
    }

    public static List<ProdottoDTO> toDTO(Collection<Prodotto> prodotti) {
        return prodotti.stream().map(ProdottoMapper::toDTO).toList();
    }
}
