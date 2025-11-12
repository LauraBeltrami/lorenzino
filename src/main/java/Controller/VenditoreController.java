package Controller;


import DTO.ProdottoDTO;
import Model.Prodotto;
import Service.ProdottoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/venditori/{venditoreId}/prodotti")
public class VenditoreController {

    private final ProdottoService prodottoService;

    public VenditoreController(ProdottoService prodottoService) { this.prodottoService = prodottoService; }

    public static record CreaProdottoReq(@NotBlank String nome, @NotNull @DecimalMin("0.00") BigDecimal prezzo) {}

    @PostMapping
    public ProdottoDTO crea(@PathVariable Long venditoreId, @Valid @RequestBody CreaProdottoReq req) {
        return prodottoService.creaProdotto(venditoreId, req.nome(), req.prezzo());
    }

    public static record AggiornaPrezzoReq(@NotNull @DecimalMin("0.00") BigDecimal prezzo) {}

    @PatchMapping("/{prodottoId}/prezzo")
    public ProdottoDTO aggiornaPrezzo(@PathVariable Long venditoreId,
                                      @PathVariable Long prodottoId,
                                      @Valid @RequestBody AggiornaPrezzoReq req) {
        return prodottoService.aggiornaPrezzo(prodottoId, req.prezzo());
    }

    @DeleteMapping("/{prodottoId}")
    public void elimina(@PathVariable Long venditoreId, @PathVariable Long prodottoId) {
        prodottoService.elimina(prodottoId);
    }
}

