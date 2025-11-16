package org.example.ids.DTO;


import org.example.ids.Model.Bundle;

import java.util.List;

public final class BundleMapper {
    private BundleMapper() {}

    public static BundleDTO toDTO(Bundle b) {
        List<BundleItemDTO> items = b.getItems().stream()
                .map(it -> new BundleItemDTO(
                        it.getProdotto().getId(),
                        it.getProdotto().getNome(),
                        it.getProdotto().getPrezzo(),
                        it.getQuantita()
                ))
                .toList();

        return new BundleDTO(
                b.getId(),
                b.getNome(),
                b.getPrezzo(),
                b.getDistributore().getId(),
                b.getDistributore().getNome(),
                items
        );
    }

    public static List<BundleDTO> toDTO(List<Bundle> bundles) {
        return bundles.stream().map(BundleMapper::toDTO).toList();
    }
}
