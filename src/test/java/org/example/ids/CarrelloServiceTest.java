package org.example.ids;


import org.example.ids.DTO.CarrelloDTO;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.example.ids.Service.CarrelloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // Disabilita il DataInitializer per avere un DB pulito
class CarrelloServiceTest {

    // Service da testare
    @Autowired
    private CarrelloService carrelloService;

    // Repository per setup e verifica
    @Autowired
    private CarrelloRepository carrelloRepo;
    @Autowired
    private CarrelloItemRepository carrelloItemRepo;
    @Autowired
    private CarrelloBundleItemRepository carrelloBundleItemRepo;
    @Autowired
    private AcquirenteRepository acquirenteRepo;
    @Autowired
    private ProdottoRepository prodottoRepo;
    @Autowired
    private BundleRepository bundleRepo;
    @Autowired
    private DistributoreRepository distributoreRepo;
    @Autowired
    private BundleItemRepository bundleItemRepo;

    // --- Dati di test comuni ---
    private Acquirente testAcquirente;
    private Prodotto prodottoApprovato;
    private Prodotto prodottoInValidazione;
    private Bundle bundleValido;
    private Bundle bundleConProdottiMisti;

    @BeforeEach
    void setUp() {
        // 1. Creo l'acquirente
        testAcquirente = acquirenteRepo.save(new Acquirente(null, "Mario", "Rossi", "mario.rossi@test.com"));

        // 2. Creo un Distributore per i prodotti e i bundle
        Distributore dist = distributoreRepo.save(new Distributore( "Distributore Carrello Test"));

        // 3. Creo i Prodotti
        prodottoApprovato = new Prodotto(null, "Salame Approvato", new BigDecimal("10.00"),50);
        prodottoApprovato.setVenditore(dist);
        prodottoApprovato.setStato(StatoProdotto.APPROVATO);
        prodottoApprovato = prodottoRepo.save(prodottoApprovato);

        prodottoInValidazione = new Prodotto(null, "Formaggio In Validazione", new BigDecimal("8.00"),50);
        prodottoInValidazione.setVenditore(dist);
        prodottoInValidazione.setStato(StatoProdotto.IN_VALIDAZIONE);
        prodottoInValidazione = prodottoRepo.save(prodottoInValidazione);

        // 4. Creo i Bundle
        // Un bundle valido (solo prodotti approvati)
        bundleValido = new Bundle(null, "Bundle Valido", new BigDecimal("50.00"), dist,50);
        bundleValido = bundleRepo.save(bundleValido);
        bundleItemRepo.save(new BundleItem(null, bundleValido, prodottoApprovato, 1));

        // Un bundle non valido (contiene prodotti non approvati)
        bundleConProdottiMisti = new Bundle(null, "Bundle Misto", new BigDecimal("60.00"), dist,50);
        bundleConProdottiMisti = bundleRepo.save(bundleConProdottiMisti);
        bundleItemRepo.save(new BundleItem(null, bundleConProdottiMisti, prodottoApprovato, 1));
        bundleItemRepo.save(new BundleItem(null, bundleConProdottiMisti, prodottoInValidazione, 1));
    }

    // --- Test loadOrCreateGraph (la logica centrale) ---

    @Test
    void testGetDettaglio_CreaNuovoCarrello_SeNonEsiste() {
        // ARRANGE
        // Nessun carrello esiste ancora
        assertThat(carrelloRepo.count()).isZero();

        // ACT
        CarrelloDTO dto = carrelloService.getDettaglio(testAcquirente.getId());

        // ASSERT
        assertThat(dto).isNotNull();
        assertThat(dto.prodotti()).isEmpty();
        assertThat(dto.bundles()).isEmpty();
        assertThat(carrelloRepo.count()).isEqualTo(1); // Ha creato un carrello
    }

    @Test
    void testGetDettaglio_CaricaCarrelloEsistente() {
        // ARRANGE
        // Creo un carrello manualmente
        Carrello c = new Carrello(null, testAcquirente);
        c = carrelloRepo.save(c);
        assertThat(carrelloRepo.count()).isEqualTo(1);

        // ACT
        CarrelloDTO dto = carrelloService.getDettaglio(testAcquirente.getId());

        // ASSERT
        assertThat(dto.carrelloId()).isEqualTo(c.getId());
        assertThat(carrelloRepo.count()).isEqualTo(1); // Non ne ha creato un altro
    }

    @Test
    void testGetDettaglio_Fail_AcquirenteInesistente() {
        // ARRANGE
        Long idInesistente = 999L;

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            carrelloService.getDettaglio(idInesistente);
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Acquirente non trovato");
    }

    // --- Test Gestione Prodotti ---

    @Test
    void testAddItem_Successo_NuovoProdotto() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long prodottoId = prodottoApprovato.getId();

        // ACT
        CarrelloDTO dto = carrelloService.addItem(acquirenteId, prodottoId, 3);

        // ASSERT
        assertThat(dto.prodotti()).hasSize(1);
        assertThat(dto.prodotti().get(0).quantita()).isEqualTo(3);
        assertThat(dto.prodotti().get(0).prodottoId()).isEqualTo(prodottoId);

        // Verifica DB
        assertThat(carrelloItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAddItem_Successo_AggiornaQuantitaEsistente() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long prodottoId = prodottoApprovato.getId();
        carrelloService.addItem(acquirenteId, prodottoId, 3); // Aggiungo 3
        assertThat(carrelloItemRepo.count()).isEqualTo(1);

        // ACT
        CarrelloDTO dto = carrelloService.addItem(acquirenteId, prodottoId, 2); // Aggiungo altri 2

        // ASSERT
        assertThat(dto.prodotti()).hasSize(1); // Ancora 1 solo item
        assertThat(dto.prodotti().get(0).quantita()).isEqualTo(5); // 3 + 2 = 5
        assertThat(carrelloItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAddItem_Fail_ProdottoNonApprovato() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long prodottoId = prodottoInValidazione.getId();

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            carrelloService.addItem(acquirenteId, prodottoId, 1);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Prodotto non approvato");
    }

    @Test
    void testUpdateQuantita_Successo_RimuoviConZero() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long prodottoId = prodottoApprovato.getId();
        CarrelloDTO dtoIniziale = carrelloService.addItem(acquirenteId, prodottoId, 5);
        assertThat(dtoIniziale.prodotti()).hasSize(1); // Pre-condizione

        // ACT (Uso removeItem, che chiama updateQuantita con 0)
        CarrelloDTO dtoAggiornato = carrelloService.removeItem(acquirenteId, prodottoId);

        // ASSERT
        assertThat(dtoAggiornato.prodotti()).isEmpty();
        assertThat(carrelloItemRepo.count()).isZero(); // Item eliminato
    }

    @Test
    void testUpdateQuantita_Fail_ProdottoNonNelCarrello() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long prodottoId = prodottoApprovato.getId(); // Prodotto non ancora nel carrello

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            carrelloService.updateQuantita(acquirenteId, prodottoId, 2);
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Articolo non presente nel carrello");
    }

    // --- Test Gestione Bundle ---

    @Test
    void testAddBundle_Successo() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long bundleId = bundleValido.getId();

        // ACT
        CarrelloDTO dto = carrelloService.addBundle(acquirenteId, bundleId, 2);

        // ASSERT
        assertThat(dto.bundles()).hasSize(1);
        assertThat(dto.bundles().get(0).quantita()).isEqualTo(2);
        assertThat(dto.bundles().get(0).bundleId()).isEqualTo(bundleId);
        assertThat(carrelloBundleItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAddBundle_Successo_AggiornaQuantitaEsistente() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long bundleId = bundleValido.getId();
        carrelloService.addBundle(acquirenteId, bundleId, 2); // Aggiungo 2

        // ACT
        CarrelloDTO dto = carrelloService.addBundle(acquirenteId, bundleId, 1); // Aggiungo 1

        // ASSERT
        assertThat(dto.bundles()).hasSize(1);
        assertThat(dto.bundles().get(0).quantita()).isEqualTo(3); // 2 + 1 = 3
        assertThat(carrelloBundleItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAddBundle_Fail_BundleContieneProdottiNonApprovati() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long bundleId = bundleConProdottiMisti.getId(); // Bundle non valido

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            carrelloService.addBundle(acquirenteId, bundleId, 1);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Il bundle contiene prodotti non approvati");
    }

    @Test
    void testRemoveBundle_Successo() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        Long bundleId = bundleValido.getId();
        CarrelloDTO dtoIniziale = carrelloService.addBundle(acquirenteId, bundleId, 2);
        assertThat(dtoIniziale.bundles()).hasSize(1); // Pre-condizione

        // ACT
        CarrelloDTO dtoAggiornato = carrelloService.removeBundle(acquirenteId, bundleId);

        // ASSERT
        assertThat(dtoAggiornato.bundles()).isEmpty();
        assertThat(carrelloBundleItemRepo.count()).isZero();
    }

    // --- Test Metodi Generali ---

    @Test
    void testClearCarrello_RimuoveTutto() {
        // ARRANGE
        Long acquirenteId = testAcquirente.getId();
        carrelloService.addItem(acquirenteId, prodottoApprovato.getId(), 2);
        carrelloService.addBundle(acquirenteId, bundleValido.getId(), 1);

        assertThat(carrelloItemRepo.count()).isEqualTo(1);
        assertThat(carrelloBundleItemRepo.count()).isEqualTo(1);

        // ACT
        CarrelloDTO dto = carrelloService.clear(acquirenteId);

        // ASSERT
        assertThat(dto.prodotti()).isEmpty();
        assertThat(dto.bundles()).isEmpty();
        assertThat(carrelloItemRepo.count()).isZero();
        assertThat(carrelloBundleItemRepo.count()).isZero();
    }
}