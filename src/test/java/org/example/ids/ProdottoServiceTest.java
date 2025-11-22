package org.example.ids;
import org.example.ids.DTO.ProdottoDTO;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.example.ids.Service.ProdottoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// Import statico per le assertion
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest // 1. Dice a JUnit di caricare l'intero contesto Spring
@Transactional  // 2. FONDAMENTALE: Annulla (rollback) tutte le modifiche al DB dopo ogni test
class ProdottoServiceTest {

    // --- Dipendenze ---

    // 3. Il Service che stiamo testando
    @Autowired
    private ProdottoService prodottoService;

    // 4. I Repository ci servono per preparare i dati (Arrange) e verificare i risultati (Assert)
    @Autowired
    private ProdottoRepository prodottoRepo;
    @Autowired
    private VenditoreRepository venditoreRepo;
    @Autowired
    private CuratoreRepository curatoreRepo;
    @Autowired
    private CertificazioneRepository certificazioneRepo;

    // --- Dati di setup ---
    private Venditore testVenditore;
    private Curatore testCuratore;
    private Prodotto prodottoInValidazione;

    // 5. Metodo eseguito PRIMA di ogni singolo @Test
    @BeforeEach
    void setUp() {
        // Creiamo dati "puliti" per ogni test, così sono indipendenti
        testVenditore = venditoreRepo.save(new Venditore( "Venditore di Test"));
        testCuratore = curatoreRepo.save(new Curatore("Curatore di Test"));

        // Creiamo un prodotto base su cui lavorare
        Prodotto p = new Prodotto();
        p.setNome("Toma da testare");
        p.setPrezzo(new BigDecimal("10.00"));
        p.setVenditore(testVenditore);
        p.setStato(StatoProdotto.IN_VALIDAZIONE);
        prodottoInValidazione = prodottoRepo.save(p);
    }

    // --- Test per creaProdotto ---

    @Test
    void testCreaProdotto_Successo() {
        // ARRANGE (Preparazione, già fatta in @BeforeEach)
        String nome = "Salame Nobile";
        BigDecimal prezzo = new BigDecimal("15.50");

        // ACT (Esegui il metodo da testare)
        ProdottoDTO dto = prodottoService.creaProdotto(testVenditore.getId(), nome, prezzo);

        // ASSERT (Verifica i risultati)
        assertThat(dto).isNotNull();
        assertThat(dto.nome()).isEqualTo(nome);
        assertThat(dto.prezzo()).isEqualByComparingTo(prezzo);
        assertThat(dto.stato()).isEqualTo(StatoProdotto.IN_VALIDAZIONE.name());

        // Verifichiamo anche che sia stato salvato correttamente nel DB
        Prodotto p = prodottoRepo.findById(dto.id()).orElseThrow();
        assertThat(p.getStato()).isEqualTo(StatoProdotto.IN_VALIDAZIONE);
        assertThat(p.getVenditore().getId()).isEqualTo(testVenditore.getId());
    }

    @Test
    void testCreaProdotto_Fail_VenditoreInesistente() {
        // ARRANGE
        Long idVenditoreInesistente = 999L;

        // ACT & ASSERT (Verifichiamo che lanci l'eccezione corretta)
        assertThatThrownBy(() -> {
            prodottoService.creaProdotto(idVenditoreInesistente, "Nome Prodotto", new BigDecimal("1.00"));
        })
                .isInstanceOf(NotFoundException.class) // Deve essere una NotFoundException
                .hasMessageContaining("Venditore non trovato"); // Controlla il messaggio
    }

    // --- Test per approvaProdotto ---

    @Test
    void testApprovaProdotto_Successo() {
        // ARRANGE (Usiamo il prodotto creato nel setup)
        Long prodottoId = prodottoInValidazione.getId();
        Long curatoreId = testCuratore.getId();

        // ACT
        ProdottoDTO dto = prodottoService.approvaProdotto(prodottoId, curatoreId, "Certificato con sigillo XYZ");

        // ASSERT
        assertThat(dto.stato()).isEqualTo(StatoProdotto.APPROVATO.name());

        // Verifichiamo nel DB
        Prodotto p = prodottoRepo.findById(prodottoId).orElseThrow();
        assertThat(p.getStato()).isEqualTo(StatoProdotto.APPROVATO);
        assertThat(p.getCertificazione()).isNotNull(); // Deve aver creato la certificazione

        // Verifichiamo che la certificazione esista e sia corretta
        assertThat(certificazioneRepo.existsByProdottoId(prodottoId)).isTrue();
        assertThat(certificazioneRepo.count()).isEqualTo(1); // Solo 1 certificazione
    }

    @Test
    void testApprovaProdotto_Fail_GiaApprovato() {
        // ARRANGE (Prima approviamo il prodotto)
        prodottoService.approvaProdotto(prodottoInValidazione.getId(), testCuratore.getId(), "Prima approvazione");

        // ACT & ASSERT (Proviamo ad approvarlo di nuovo)
        assertThatThrownBy(() -> {
            prodottoService.approvaProdotto(prodottoInValidazione.getId(), testCuratore.getId(), "Seconda approvazione");
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Già approvato");
    }

    @Test
    void testApprovaProdotto_Fail_ProdottoRifiutato() {
        // ARRANGE (Prima rifiutiamo il prodotto)
        prodottoInValidazione.setStato(StatoProdotto.RIFIUTATO);
        prodottoRepo.save(prodottoInValidazione);

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            prodottoService.approvaProdotto(prodottoInValidazione.getId(), testCuratore.getId(), "Test");
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Rifiutato: non approvabile");
    }

    // --- Test per rifiutaProdotto ---

    @Test
    void testRifiutaProdotto_Successo() {
        // ARRANGE
        Long prodottoId = prodottoInValidazione.getId();

        // ACT
        ProdottoDTO dto = prodottoService.rifiutaProdotto(prodottoId, "Non conforme ai requisiti");

        // ASSERT
        assertThat(dto.stato()).isEqualTo(StatoProdotto.RIFIUTATO.name());

        // Verifichiamo nel DB
        Prodotto p = prodottoRepo.findById(prodottoId).orElseThrow();
        assertThat(p.getStato()).isEqualTo(StatoProdotto.RIFIUTATO);
        assertThat(p.getCertificazione()).isNull(); // Non deve esserci certificazione
        assertThat(certificazioneRepo.count()).isZero();
    }

    // --- Test per listaVendibili ---

    @Test
    void testListaVendibili_RestituisceSoloApprovati() {
        // ARRANGE
        // 1. Il prodotto del setup è IN_VALIDAZIONE

        // 2. Creiamo un prodotto RIFIUTATO
        Prodotto rifiutato = new Prodotto();
        rifiutato.setNome("Rifiutato");
        rifiutato.setPrezzo(new BigDecimal("1.00"));
        rifiutato.setVenditore(testVenditore);
        rifiutato.setStato(StatoProdotto.RIFIUTATO);
        prodottoRepo.save(rifiutato);

        // 3. Creiamo un prodotto APPROVATO
        Prodotto approvato = new Prodotto();
        approvato.setNome("Approvato");
        approvato.setPrezzo(new BigDecimal("2.00"));
        approvato.setVenditore(testVenditore);
        approvato.setStato(StatoProdotto.APPROVATO);
        prodottoRepo.save(approvato);

        // ACT
        List<ProdottoDTO> vendibili = prodottoService.listaVendibili();

        // ASSERT
        assertThat(vendibili).hasSize(1); // Deve trovare solo 1 prodotto
        assertThat(vendibili.get(0).nome()).isEqualTo("Approvato");
    }
}