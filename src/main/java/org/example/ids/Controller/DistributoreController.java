package org.example.ids.Controller;


import org.example.ids.DTO.BundleDTO;
import org.example.ids.Service.DistributoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/distributori/{distributoreId}")
public class DistributoreController {

    private final DistributoreService distributoreService;

    public DistributoreController(DistributoreService distributoreService) {
        this.distributoreService = distributoreService;
    }

    // ----- bundle -----
    public static record CreaBundleReq(@NotBlank String nome,
                                       @NotNull @DecimalMin("0.00") BigDecimal prezzo) {
    }

    @PostMapping("/bundles")
    public BundleDTO creaBundle(@PathVariable Long distributoreId, @Valid @RequestBody CreaBundleReq req) {
        return distributoreService.creaBundle(distributoreId, req.nome(), req.prezzo());
    }

    @GetMapping("/bundles")
    public List<BundleDTO> listaBundles(@PathVariable Long distributoreId) {
        return distributoreService.listaBundles(distributoreId);
    }

    @GetMapping("/bundles/{bundleId}")
    public BundleDTO getBundle(@PathVariable Long distributoreId, @PathVariable Long bundleId) {
        return distributoreService.getBundle(bundleId); // opz.: verifica ownership
    }

    public static record AddItemBundleReq(@NotNull Long prodottoId, @Min(1) int quantita) {
    }

    @PostMapping("/bundles/{bundleId}/items")
    public BundleDTO aggiungiItem(@PathVariable Long distributoreId,
                                  @PathVariable Long bundleId,
                                  @Valid @RequestBody AddItemBundleReq req) {
        return distributoreService.aggiungiProdottoABundle(bundleId, req.prodottoId(), req.quantita());
    }

    public static record UpdateQuantitaReq(@Min(0) int quantita) {
    }

    @PatchMapping("/bundles/{bundleId}/items/{prodottoId}")
    public BundleDTO aggiornaQuantitaItem(@PathVariable Long distributoreId,
                                          @PathVariable Long bundleId,
                                          @PathVariable Long prodottoId,
                                          @Valid @RequestBody UpdateQuantitaReq req) {
        return distributoreService.aggiornaQuantitaInBundle(bundleId, prodottoId, req.quantita());
    }

    @DeleteMapping("/bundles/{bundleId}/items/{prodottoId}")
    public BundleDTO rimuoviItem(@PathVariable Long distributoreId,
                                 @PathVariable Long bundleId,
                                 @PathVariable Long prodottoId) {
        return distributoreService.rimuoviProdottoDaBundle(bundleId, prodottoId);
    }

    public static record UpdatePrezzoReq(@NotNull @DecimalMin("0.00") BigDecimal prezzo) {
    }

    @PatchMapping("/bundles/{bundleId}/prezzo")
    public BundleDTO aggiornaPrezzo(@PathVariable Long distributoreId,
                                    @PathVariable Long bundleId,
                                    @Valid @RequestBody UpdatePrezzoReq req) {
        return distributoreService.aggiornaPrezzoBundle(bundleId, req.prezzo());
    }
}
