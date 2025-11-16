package org.example.ids.Controller;


import org.example.ids.DTO.EventoDTO;
import org.example.ids.Service.EventoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/inviti")
public class InvitiController {

    private final EventoService eventoService;

    public InvitiController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // ========= EVENTI =========

    public static record CreaEventoReq(
            @NotBlank String titolo,
            String descrizione,
            @NotBlank String luogo,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inizio,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fine
    ) {}

    /** Crea un evento per un animatore. */
    @PostMapping("/animatori/{animatoreId}/eventi")
    public EventoDTO creaEvento(@PathVariable @Positive Long animatoreId,
                                @Valid @RequestBody CreaEventoReq req) {
        return eventoService.creaEvento(animatoreId, req.titolo(), req.descrizione(),
                req.luogo(), req.inizio(), req.fine());
    }

    /** Lista eventi di un animatore (con invitati). */
    @GetMapping("/animatori/{animatoreId}/eventi")
    public List<EventoDTO> listaEventiAnimatore(@PathVariable @Positive Long animatoreId) {
        return eventoService.listaEventiAnimatore(animatoreId);
    }

    /** Dettaglio evento (invitati inclusi). */
    @GetMapping("/eventi/{eventoId}")
    public EventoDTO getEvento(@PathVariable @Positive Long eventoId) {
        return eventoService.getEvento(eventoId);
    }

    // ========= INVITI =========

    public static record InvitaReq(@NotNull @Positive Long venditoreId, String nota) {}

    /** Invita un venditore (o distributore) all’evento. */
    @PostMapping("/eventi/{eventoId}/inviti")
    public EventoDTO invita(@PathVariable @Positive Long eventoId,
                            @Valid @RequestBody InvitaReq req) {
        return eventoService.invita(eventoId, req.venditoreId(), req.nota());
    }

    public static record InvitaBulkReq(@NotNull List<@Positive Long> venditoreIds, String nota) {}

    /** Invito bulk: più venditori in una sola chiamata. */
    @PostMapping("/eventi/{eventoId}/inviti/bulk")
    public EventoDTO invitaBulk(@PathVariable @Positive Long eventoId,
                                @Valid @RequestBody InvitaBulkReq req) {
        // semplice loop: usa lo stesso servizio per mantenere le regole (unique constraint)
        req.venditoreIds().forEach(id -> eventoService.invita(eventoId, id, req.nota()));
        return eventoService.getEvento(eventoId);
    }

    /** Rimuove l’invito di un venditore. */
    @DeleteMapping("/eventi/{eventoId}/inviti/{venditoreId}")
    public EventoDTO rimuoviInvito(@PathVariable @Positive Long eventoId,
                                   @PathVariable @Positive Long venditoreId) {
        return eventoService.rimuoviInvito(eventoId, venditoreId);
    }
}

