package org.example.ids;


import org.example.ids.DTO.RichiestaApprovazioneDTO;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.Animatore;
import org.example.ids.Model.Curatore;
import org.example.ids.Model.Venditore;
import org.example.ids.Repository.AnimatoreRepository;
import org.example.ids.Repository.CuratoreRepository;
import org.example.ids.Repository.VenditoreRepository;
import org.example.ids.Service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Abilita Mockito, molto più veloce di @SpringBootTest
class AdminServiceTest {

    // 1. Creiamo i "finti" repository
    @Mock private VenditoreRepository venditoreRepo;
    @Mock private AnimatoreRepository animatoreRepo;
    @Mock private CuratoreRepository curatoreRepo;

    // 2. Iniettiamo i finti repository dentro il service reale
    @InjectMocks private AdminService adminService;

    // --- Test getUtentiInAttesa (Aggregazione) ---

    @Test
    void testGetUtentiInAttesa_AggregaCorrettamente() {
        // ARRANGE: Prepariamo i dati finti che i repo devono restituire
        Venditore v = new Venditore("Salumi Rossi");
        // 2. Setto l'ID a mano (solo per il test, per simularne uno salvato)
        v.setId(1L);

        Animatore a = new Animatore("DJ Francesco");
        a.setId(2L);

        Curatore c = new Curatore("Mario Arte");
        c.setId(3L);

        // Istruiamo i mock: "Quando qualcuno ti chiama, restituisci questa lista"
        when(venditoreRepo.findByApprovatoFalse()).thenReturn(List.of(v));
        when(animatoreRepo.findByApprovatoFalse()).thenReturn(List.of(a));
        when(curatoreRepo.findByApprovatoFalse()).thenReturn(List.of(c));

        // ACT: Chiamiamo il metodo vero
        List<RichiestaApprovazioneDTO> risultato = adminService.getUtentiInAttesa();

        // ASSERT: Verifichiamo che abbia messo tutto insieme
        assertThat(risultato).hasSize(3); // 1 venditore + 1 animatore + 1 curatore

        // Verifichiamo che i ruoli siano stati assegnati giusti (l'ordine dipende dall'ordine delle chiamate nel service)
        assertThat(risultato)
                .extracting(RichiestaApprovazioneDTO::ruolo)
                .containsExactlyInAnyOrder("VENDITORE", "ANIMATORE", "CURATORE");

        assertThat(risultato)
                .extracting(RichiestaApprovazioneDTO::nome)
                .contains("Salumi Rossi", "DJ Francesco", "Mario Arte");
    }

    @Test
    void testGetUtentiInAttesa_ListaVuota() {
        // ARRANGE: Nessuno in attesa
        when(venditoreRepo.findByApprovatoFalse()).thenReturn(List.of());
        when(animatoreRepo.findByApprovatoFalse()).thenReturn(List.of());
        when(curatoreRepo.findByApprovatoFalse()).thenReturn(List.of());

        // ACT
        List<RichiestaApprovazioneDTO> risultato = adminService.getUtentiInAttesa();

        // ASSERT
        assertThat(risultato).isEmpty();
    }

    // --- Test approvaUtente (Switch logic) ---

    @Test
    void testApprovaUtente_Venditore_Successo() {
        // ARRANGE
        Long id = 10L;
        Venditore v = new Venditore( "Test Venditore");
        v.setId(id);
        v.setApprovato(false); // Partenza

        when(venditoreRepo.findById(id)).thenReturn(Optional.of(v));

        // ACT
        adminService.approvaUtente(id, "VENDITORE");

        // ASSERT
        assertThat(v.isApprovato()).isTrue(); // Deve essere diventato true
    }

    @Test
    void testApprovaUtente_Animatore_Successo() {
        // ARRANGE
        Long id = 20L;
        Animatore a = new Animatore("Test Animatore");
        a.setId(id);
        a.setApprovato(false);

        when(animatoreRepo.findById(id)).thenReturn(Optional.of(a));

        // ACT
        adminService.approvaUtente(id, "ANIMATORE"); // Case insensitive test? Nel codice service avevi fatto .toUpperCase()

        // ASSERT
        assertThat(a.isApprovato()).isTrue();
    }

    @Test
    void testApprovaUtente_RuoloSconosciuto_LanciaEccezione() {
        // ACT & ASSERT
        assertThatThrownBy(() -> {
            adminService.approvaUtente(1L, "ASTRONAUTA");
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ruolo non valido");
    }

    @Test
    void testApprovaUtente_UtenteNonTrovato_LanciaEccezione() {
        // ARRANGE
        Long idInesistente = 999L;
        when(venditoreRepo.findById(idInesistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            adminService.approvaUtente(idInesistente, "VENDITORE");
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Venditore non trovato");
    }
}