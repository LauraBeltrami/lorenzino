package DTO;


import java.math.BigDecimal;
import java.util.List;

public record CarrelloDTO(
        Long carrelloId,
        Long acquirenteId,
        String acquirenteNome,
        List<RigaProdottoDTO> prodotti,
        List<RigaBundleDTO> bundles,
        int numeroArticoli,
        BigDecimal totale
) {}
