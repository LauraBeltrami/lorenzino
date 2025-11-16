package org.example.ids.Controller;


import org.example.ids.DTO.ProdottoDTO;
import org.example.ids.Service.ProdottoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/curatori/{curatoreId}/prodotti")
public class CuratoreController {

    private final ProdottoService prodottoService;

    public CuratoreController(ProdottoService prodottoService) { this.prodottoService = prodottoService; }

    public static record ApprovaReq(@NotBlank String descrizioneCertificazione) {}

    @PostMapping("/{prodottoId}/approva")
    public ProdottoDTO approva(@PathVariable Long curatoreId,
                               @PathVariable Long prodottoId,
                               @Valid @RequestBody ApprovaReq req) {
        return prodottoService.approvaProdotto(prodottoId, curatoreId, req.descrizioneCertificazione());
    }

    public static record RifiutaReq(String motivo) {}

    @PostMapping("/{prodottoId}/rifiuta")
    public ProdottoDTO rifiuta(@PathVariable Long curatoreId,
                               @PathVariable Long prodottoId,
                               @RequestBody RifiutaReq req) {
        return prodottoService.rifiutaProdotto(prodottoId, req != null ? req.motivo() : null);
    }
}

