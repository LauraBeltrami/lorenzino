package org.example.ids;


import org.example.ids.DTO.EventoDTO;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.Animatore;
import org.example.ids.Model.Evento;
import org.example.ids.Model.Venditore;
import org.example.ids.Repository.AnimatoreRepository;
import org.example.ids.Repository.EventoRepository;
import org.example.ids.Repository.InvitoEventoRepository;
import org.example.ids.Repository.VenditoreRepository;
import org.example.ids.Service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // Disabilita il DataInitializer per avere un DB pulito
class EventoServiceTest {

    // Service da testare
    @Autowired
    private EventoService eventoService;

    // Repository per setup e verifica
    @Autowired
    private AnimatoreRepository animatoreRepo;
    @Autowired
    private EventoRepository eventoRepo;
    @Autowired
    private InvitoEventoRepository invitoRepo;
    @Autowired
    private VenditoreRepository venditoreRepo;

    // --- Dati di test comuni ---
    private Animatore testAnimatore;
    private Venditore testVenditore;
    private Evento testEvento;
    private LocalDateTime inizio;
    private LocalDateTime fine;

    @BeforeEach
    void setUp() {
        // 1. Creo attori comuni
        testAnimatore = animatoreRepo.save(new Animatore( "Pro Loco Eventi"));
        testVenditore = venditoreRepo.save(new Venditore( "Salumi Rossi"));

        // 2. Definisco date valide
        inizio = LocalDateTime.now().plusDays(10);
        fine = LocalDateTime.now().plusDays(11);

        // 3. Creo un evento base per i test di invito/rimozione
        testEvento = new Evento(null, "Fiera del Tartufo", "Descrizione...", "Alba",
                inizio.minusDays(5), // Un evento passato
                inizio.minusDays(4),
                testAnimatore);
        testEvento = eventoRepo.save(testEvento);
    }

    // --- Test creaEvento ---

    @Test
    void testCreaEvento_Successo() {
        // ARRANGE
        String titolo = "Sagra della Nocciola";

        // ACT
        EventoDTO dto = eventoService.creaEvento(
                testAnimatore.getId(),
                titolo,
                "Descrizione sagra",
                "Cortemilia",
                inizio,
                fine
        );

        // ASSERT
        assertThat(dto).isNotNull();
        assertThat(dto.titolo()).isEqualTo(titolo);
        assertThat(dto.luogo()).isEqualTo("Cortemilia");
        assertThat(dto.animatoreId()).isEqualTo(testAnimatore.getId());
        assertThat(dto.animatoreNome()).isEqualTo(testAnimatore.getNome()); // Verifica che il graph/mapper funzioni
        assertThat(dto.inviti()).isEmpty();

        // Verifica DB (ora ci sono 2 eventi, quello del setup e questo)
        assertThat(eventoRepo.count()).isEqualTo(2);
    }

    @Test
    void testCreaEvento_Fail_DateInvalide() {
        // ARRANGE (Date invertite)
        LocalDateTime inizioErrato = fine;
        LocalDateTime fineErrata = inizio;

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            eventoService.creaEvento(
                    testAnimatore.getId(),
                    "Titolo", "Desc", "Luogo",
                    inizioErrato, // Inizio è DOPO la fine
                    fineErrata
            );
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Inizio deve precedere Fine");
    }

    @Test
    void testCreaEvento_Fail_AnimatoreInesistente() {
        // ARRANGE
        Long idAnimatoreInesistente = 999L;

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            eventoService.creaEvento(
                    idAnimatoreInesistente,
                    "Titolo", "Desc", "Luogo",
                    inizio, fine
            );
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Animatore non trovato");
    }

    // --- Test listaEventiAnimatore ---

    @Test
    void testListaEventiAnimatore_Successo() {
        // ARRANGE
        // testEvento è già stato creato nel setup per testAnimatore
        // Creo un altro animatore e un altro evento per essere sicuro che filtri
        Animatore altroAnimatore = animatoreRepo.save(new Animatore( "Altro Animatore"));
        eventoService.creaEvento(altroAnimatore.getId(), "Altro Evento", "...", "...", inizio, fine);

        // ACT
        List<EventoDTO> dtos = eventoService.listaEventiAnimatore(testAnimatore.getId());

        // ASSERT
        assertThat(dtos).hasSize(1); // Deve trovare solo l'evento del setup
        assertThat(dtos.get(0).id()).isEqualTo(testEvento.getId());
    }

    @Test
    void testListaEventiAnimatore_NessunEvento() {
        // ARRANGE
        Animatore animatoreSenzaEventi = animatoreRepo.save(new Animatore( "Animatore Pigro"));

        // ACT
        List<EventoDTO> dtos = eventoService.listaEventiAnimatore(animatoreSenzaEventi.getId());

        // ASSERT
        assertThat(dtos).isEmpty();
    }

    // --- Test Inviti (invita / rimuoviInvito) ---

    @Test
    void testInvita_Successo() {
        // ARRANGE
        Long eventoId = testEvento.getId();
        Long venditoreId = testVenditore.getId();
        assertThat(invitoRepo.count()).isZero(); // Pre-condizione

        // ACT
        EventoDTO dto = eventoService.invita(eventoId, venditoreId, "Porta il salame!");

        // ASSERT
        assertThat(dto.inviti()).hasSize(1);
        assertThat(dto.inviti().get(0).nota()).isEqualTo("Porta il salame!");
        assertThat(dto.inviti().get(0).venditoreId()).isEqualTo(venditoreId);

        // Verifica DB
        assertThat(invitoRepo.count()).isEqualTo(1);
        assertThat(invitoRepo.findByEventoIdAndVenditoreId(eventoId, venditoreId)).isPresent();
    }

    @Test
    void testInvita_Fail_GiaInvitato() {
        // ARRANGE
        Long eventoId = testEvento.getId();
        Long venditoreId = testVenditore.getId();
        eventoService.invita(eventoId, venditoreId, "Nota 1"); // Invito la prima volta

        // ACT & ASSERT (Invito la seconda volta)
        assertThatThrownBy(() -> {
            eventoService.invita(eventoId, venditoreId, "Nota 2");
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Già invitato");
    }

    @Test
    void testInvita_Fail_VenditoreInesistente() {
        // ARRANGE
        Long eventoId = testEvento.getId();
        Long venditoreInesistenteId = 999L;

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            eventoService.invita(eventoId, venditoreInesistenteId, "Nota");
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Venditore/Distributore non trovato");
    }

    @Test
    void testRimuoviInvito_Successo() {
        // ARRANGE
        Long eventoId = testEvento.getId();
        Long venditoreId = testVenditore.getId();
        eventoService.invita(eventoId, venditoreId, "Nota 1"); // Creo l'invito
        assertThat(invitoRepo.count()).isEqualTo(1); // Pre-condizione

        // ACT
        EventoDTO dto = eventoService.rimuoviInvito(eventoId, venditoreId);

        // ASSERT
        assertThat(dto.inviti()).isEmpty();

        // Verifica DB
        assertThat(invitoRepo.count()).isZero();
    }

    @Test
    void testRimuoviInvito_Fail_InvitoInesistente() {
        // ARRANGE
        Long eventoId = testEvento.getId();
        Long venditoreId = testVenditore.getId();
        // L'invito non esiste
        assertThat(invitoRepo.count()).isZero();

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            eventoService.rimuoviInvito(eventoId, venditoreId);
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Invito non trovato");
    }
}