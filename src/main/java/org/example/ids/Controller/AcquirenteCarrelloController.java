package org.example.ids.Controller;


import org.example.ids.DTO.CarrelloDTO;
import org.example.ids.Service.CarrelloService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/acquirenti/{acquirenteId}/carrello")
public class AcquirenteCarrelloController {

    private final CarrelloService carrelloService;
    public AcquirenteCarrelloController(CarrelloService carrelloService) { this.carrelloService = carrelloService; }

    @GetMapping public CarrelloDTO getDettaglio(@PathVariable Long acquirenteId) {
        return carrelloService.getDettaglio(acquirenteId);
    }

    @DeleteMapping public CarrelloDTO svuota(@PathVariable Long acquirenteId) {
        return carrelloService.clear(acquirenteId);
    }

    // prodotti
    public static record AddItemReq(@NotNull Long prodottoId, @Min(1) int quantita) {}
    @PostMapping("/items")
    public CarrelloDTO addItem(@PathVariable Long acquirenteId, @Valid @RequestBody AddItemReq req) {
        return carrelloService.addItem(acquirenteId, req.prodottoId(), req.quantita());
    }
    public static record UpdateQuantitaReq(@Min(0) int quantita) {}
    @PatchMapping("/items/{prodottoId}") public CarrelloDTO updateQuantita(
            @PathVariable Long acquirenteId, @PathVariable Long prodottoId, @Valid @RequestBody UpdateQuantitaReq req) {
        return carrelloService.updateQuantita(acquirenteId, prodottoId, req.quantita());
    }
    @DeleteMapping("/items/{prodottoId}") public CarrelloDTO removeItem(
            @PathVariable Long acquirenteId, @PathVariable Long prodottoId) {
        return carrelloService.removeItem(acquirenteId, prodottoId);
    }

    // bundles
    public static record AddBundleReq(@NotNull Long bundleId, @Min(1) int quantita) {}
    @PostMapping("/bundles")
    public CarrelloDTO addBundle(@PathVariable Long acquirenteId, @Valid @RequestBody AddBundleReq req) {
        return carrelloService.addBundle(acquirenteId, req.bundleId(), req.quantita());
    }
    @PatchMapping("/bundles/{bundleId}") public CarrelloDTO updateBundle(
            @PathVariable Long acquirenteId, @PathVariable Long bundleId, @Valid @RequestBody UpdateQuantitaReq req) {
        return carrelloService.updateQuantitaBundle(acquirenteId, bundleId, req.quantita());
    }
    @DeleteMapping("/bundles/{bundleId}") public CarrelloDTO removeBundle(
            @PathVariable Long acquirenteId, @PathVariable Long bundleId) {
        return carrelloService.removeBundle(acquirenteId, bundleId);
    }

    @PostMapping("/acquista")
    public CarrelloDTO acquista(@PathVariable Long acquirenteId) {
        return carrelloService.acquista(acquirenteId);
    }
}
