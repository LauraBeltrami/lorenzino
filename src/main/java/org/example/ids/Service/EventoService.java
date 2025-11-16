package org.example.ids.Service;


import org.example.ids.DTO.EventoDTO;
import org.example.ids.DTO.EventoMapper;
import org.example.ids.DTO.InvitoDTO;
import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventoService implements InvitoHandler,PrenotazioneHandler{

    private final AnimatoreRepository animatoreRepo;
    private final EventoRepository eventoRepo;
    private final InvitoEventoRepository invitoRepo;
    private final AcquirenteRepository acquirenteRepo;
    private final VenditoreRepository venditoreRepo;

    public EventoService(AnimatoreRepository animatoreRepo,
                         EventoRepository eventoRepo,
                         InvitoEventoRepository invitoRepo,
                         VenditoreRepository venditoreRepo,
                         AcquirenteRepository acquirenteRepo) {
        this.animatoreRepo = animatoreRepo;
        this.eventoRepo = eventoRepo;
        this.invitoRepo = invitoRepo;
        this.venditoreRepo = venditoreRepo;
        this.acquirenteRepo = acquirenteRepo;
    }

    // --- Eventi ---
    public EventoDTO creaEvento(Long animatoreId, String titolo, String descrizione,
                                String luogo, LocalDateTime inizio, LocalDateTime fine) {
        if (!inizio.isBefore(fine)) throw new BusinessException("Inizio deve precedere Fine.");
        Animatore a = animatoreRepo.findById(animatoreId)
                .orElseThrow(() -> new NotFoundException("Animatore non trovato: " + animatoreId));
        Evento e = eventoRepo.save(new Evento(null, titolo, descrizione, luogo, inizio, fine, a));
        e = eventoRepo.findGraphById(e.getId()).orElse(e);
        return EventoMapper.toDTO(e);
    }

    public EventoDTO getEvento(Long eventoId) {
        Evento e = eventoRepo.findGraphById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento non trovato: " + eventoId));
        return EventoMapper.toDTO(e);
    }

    public List<EventoDTO> listaEventiAnimatore(Long animatoreId) {
        return EventoMapper.toDTO(eventoRepo.findGraphByAnimatoreId(animatoreId));
    }

    // --- Inviti (solo creazione/rimozione) ---
    public EventoDTO invita(Long eventoId, Long venditoreId, String nota) {
        Evento e = eventoRepo.findGraphById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento non trovato: " + eventoId));
        Venditore v = venditoreRepo.findById(venditoreId)
                .orElseThrow(() -> new NotFoundException("Venditore/Distributore non trovato: " + venditoreId));

        invitoRepo.findByEventoIdAndVenditoreId(eventoId, venditoreId).ifPresent(x -> {
            throw new BusinessException("Già invitato a questo evento.");
        });

        e.getInviti().add(new InvitoEvento(e, v,StatoInvito.IN_ATTESA, nota));
        return EventoMapper.toDTO(e);
    }

    public EventoDTO rimuoviInvito(Long eventoId, Long venditoreId) {
        Evento e = eventoRepo.findGraphById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento non trovato: " + eventoId));
        InvitoEvento inv = invitoRepo.findByEventoIdAndVenditoreId(eventoId, venditoreId)
                .orElseThrow(() -> new NotFoundException("Invito non trovato."));
        e.getInviti().remove(inv);
        invitoRepo.delete(inv);
        return EventoMapper.toDTO(e);
    }
    @Override
    public EventoDTO prenotaEvento(Long eventoId, Long acquirenteId) {
        // 1. Carica le entità (siamo in una transazione, il lazy loading è ok)
        Evento e = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento non trovato: " + eventoId));

        Acquirente a = acquirenteRepo.findById(acquirenteId)
                .orElseThrow(() -> new NotFoundException("Acquirente non trovato: " + acquirenteId));

        // 2. Logica di business: controlla se è già prenotato
        // Grazie al Set.add(), potremmo anche evitarlo, ma è più pulito per i messaggi d'errore.
        if (e.getPrenotazioni().contains(a)) {
            throw new BusinessException("Acquirente già prenotato per questo evento.");
        }

        // 3. Esegui la modifica (JPA la salverà al commit)
        e.getPrenotazioni().add(a);

        // 4. Ritorna il DTO aggiornato
        return EventoMapper.toDTO(e);
    }

    /**
     * Rimuove la prenotazione di un Acquirente da un Evento.
     */
    @Override
    public EventoDTO annullaPrenotazione(Long eventoId, Long acquirenteId) {
        // 1. Carica le entità
        Evento e = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new NotFoundException("Evento non trovato: " + eventoId));

        Acquirente a = acquirenteRepo.findById(acquirenteId)
                .orElseThrow(() -> new NotFoundException("Acquirente non trovato: " + acquirenteId));

        // 2. Logica di business: rimuovi solo se esiste
        boolean rimosso = e.getPrenotazioni().remove(a);

        if (!rimosso) {
            throw new NotFoundException("Prenotazione non trovata per questo acquirente.");
        }

        // 3. Ritorna il DTO aggiornato
        return EventoMapper.toDTO(e);
    }

    private InvitoEvento trovaInvito(Long eventoId, Long venditoreId) {
        return invitoRepo.findByEventoIdAndVenditoreId(eventoId, venditoreId)
                .orElseThrow(() -> new NotFoundException("Invito non trovato."));
    }

    @Override
    public InvitoDTO rispondiInvito(Long eventoId, Long venditoreId,StatoInvito stato) {
        InvitoEvento invito = trovaInvito(eventoId, venditoreId);

        if (invito.getStato() == StatoInvito.ACCETTATO) {
            throw new BusinessException("Invito già accettato.");
        }

        invito.setStato(stato);
        // Non serve .save(), @Transactional fa il dirty checking

        return new InvitoDTO(venditoreId,invito.getVenditore().getNome(), invito.getNota());
    }


}

