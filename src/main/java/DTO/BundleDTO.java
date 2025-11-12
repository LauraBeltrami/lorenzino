package DTO;


import java.math.BigDecimal;
import java.util.List;

public record BundleDTO(
        Long id,
        String nome,
        BigDecimal prezzo,       // deciso dal distributore
        Long distributoreId,
        String distributoreNome,
        List<BundleItemDTO> items
) {}

