package DTO;


import java.math.BigDecimal;

public record BundleItemDTO(
        Long prodottoId,
        String prodottoNome,
        BigDecimal prezzoProdotto, // opzionale ma utile in UI
        int quantita
) {}
