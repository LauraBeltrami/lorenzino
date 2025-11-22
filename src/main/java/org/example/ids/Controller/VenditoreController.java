package org.example.ids.Controller;


import jakarta.validation.constraints.*;
import org.example.ids.DTO.InvitoDTO;
import org.example.ids.DTO.ProdottoDTO;
import org.example.ids.Model.StatoInvito;
import org.example.ids.Service.InvitoHandler;
import org.example.ids.Service.ProdottoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/venditori/{venditoreId}/prodotti")
public class VenditoreController {

    private final ProdottoService prodottoService;

    private final InvitoHandler eventoService;

    public VenditoreController(ProdottoService prodottoService,
                               InvitoHandler eventoService) {
        this.prodottoService = prodottoService;
        this.eventoService = eventoService;
    }

    public static record CreaProdottoReq(@NotBlank String nome, @NotNull @DecimalMin("0.00") BigDecimal prezzo, @Min(0) int quantita) {}

    @PostMapping
    public ProdottoDTO crea(@PathVariable Long venditoreId, @Valid @RequestBody CreaProdottoReq req) {
        return prodottoService.creaProdotto(venditoreId, req.nome(), req.prezzo(), req.quantita());
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

    @PostMapping("/{eventoId}/accetta")
    public InvitoDTO accettaInvito(
            @PathVariable @Positive Long venditoreId,
            @PathVariable @Positive Long eventoId,
            @PathVariable @NotNull StatoInvito statoInvito) {

        return eventoService.rispondiInvito(eventoId, venditoreId, statoInvito);
    }
}

