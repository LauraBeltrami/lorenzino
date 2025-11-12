package Service;


import DTO.EventoDTO;
import DTO.EventoMapper;
import Model.Animatore;
import Model.Evento;
import Model.InvitoEvento;
import Model.Venditore;
import Repository.AnimatoreRepository;
import Repository.EventoRepository;
import Repository.InvitoEventoRepository;
import Repository.VenditoreRepository;
import Exceptions.BusinessException;
import Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventoService {

    private final AnimatoreRepository animatoreRepo;
    private final EventoRepository eventoRepo;
    private final InvitoEventoRepository invitoRepo;
    private final VenditoreRepository venditoreRepo;

    public EventoService(AnimatoreRepository animatoreRepo,
                         EventoRepository eventoRepo,
                         InvitoEventoRepository invitoRepo,
                         VenditoreRepository venditoreRepo) {
        this.animatoreRepo = animatoreRepo;
        this.eventoRepo = eventoRepo;
        this.invitoRepo = invitoRepo;
        this.venditoreRepo = venditoreRepo;
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

        e.getInviti().add(new InvitoEvento(e, v, nota));
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
}

