package org.example.ids.Controller;
import jakarta.validation.constraints.Positive;
import org.example.ids.Service.PrenotazioneHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.example.ids.DTO.EventoDTO;
@Validated
@RestController
@RequestMapping("/api/acquirenti/{acquirenteId}/prenotazioni") // Path di base
public class AcquirenteController {

    private final PrenotazioneHandler eventoService;

    public AcquirenteController(PrenotazioneHandler eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * Un acquirente si prenota a un evento.
     */
    @PostMapping("/eventi/{eventoId}")
    public EventoDTO prenotaEvento(
            @PathVariable @Positive Long acquirenteId,
            @PathVariable @Positive Long eventoId) {

        return eventoService.prenotaEvento(eventoId, acquirenteId);
    }

    /**
     * Un acquirente annulla la sua prenotazione a un evento.
     */
    @DeleteMapping("/eventi/{eventoId}")
    public EventoDTO annullaPrenotazione(
            @PathVariable @Positive Long acquirenteId,
            @PathVariable @Positive Long eventoId) {

        return eventoService.annullaPrenotazione(eventoId, acquirenteId);
    }
}