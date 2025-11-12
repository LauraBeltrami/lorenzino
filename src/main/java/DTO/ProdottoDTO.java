package DTO;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdottoDTO(
        Long id,
        String nome,
        BigDecimal prezzo,
        String stato,          // "IN_VALIDAZIONE" | "APPROVATO" | "RIFIUTATO"
        Long venditoreId,
        String venditoreNome,
        CertificazioneDTO certificazione // null se non approvato
) {}


