package org.example.ids;



import org.example.ids.DTO.BundleDTO;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.Bundle;
import org.example.ids.Model.Distributore;
import org.example.ids.Model.Prodotto;
import org.example.ids.Model.StatoProdotto;
import org.example.ids.Repository.*;
import org.example.ids.Service.DistributoreService;
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
class DistributoreServiceTest {

    // Service da testare
    @Autowired
    private DistributoreService distributoreService;

    // Repository per setup e verifica
    @Autowired
    private DistributoreRepository distributoreRepo;
    @Autowired
    private ProdottoRepository prodottoRepo;
    @Autowired
    private BundleRepository bundleRepo;
    @Autowired
    private BundleItemRepository bundleItemRepo;
    @Autowired
    private VenditoreRepository venditoreRepo; // Necessario per salvare i prodotti

    // Dati di test comuni
    private Distributore testDistributore;
    private Prodotto prodottoApprovato1;
    private Prodotto prodottoApprovato2;
    private Prodotto prodottoInValidazione;

    @BeforeEach
    void setUp() {
        // Pulisco i repo per sicurezza (anche se @Transactional dovrebbe bastare)
        bundleItemRepo.deleteAll();
        bundleRepo.deleteAll();
        prodottoRepo.deleteAll();
        distributoreRepo.deleteAll();
        venditoreRepo.deleteAll();

        // 1. Creo un Distributore (che è anche un Venditore)
        testDistributore = new Distributore(null, "Distributore Test");
        testDistributore = distributoreRepo.save(testDistributore);

        // 2. Creo prodotti (che richiedono un Venditore)
        prodottoApprovato1 = new Prodotto(null, "Salame", new BigDecimal("10.00"));
        prodottoApprovato1.setVenditore(testDistributore);
        prodottoApprovato1.setStato(StatoProdotto.APPROVATO); // Fondamentale per la logica del bundle
        prodottoApprovato1 = prodottoRepo.save(prodottoApprovato1);

        prodottoApprovato2 = new Prodotto(null, "Toma", new BigDecimal("5.00"));
        prodottoApprovato2.setVenditore(testDistributore);
        prodottoApprovato2.setStato(StatoProdotto.APPROVATO);
        prodottoApprovato2 = prodottoRepo.save(prodottoApprovato2);

        prodottoInValidazione = new Prodotto(null, "Grissini", new BigDecimal("2.00"));
        prodottoInValidazione.setVenditore(testDistributore);
        prodottoInValidazione.setStato(StatoProdotto.IN_VALIDAZIONE); // Non approvato
        prodottoInValidazione = prodottoRepo.save(prodottoInValidazione);
    }

    // --- Test creaBundle ---

    @Test
    void testCreaBundle_Successo() {
        // ACT
        BundleDTO dto = distributoreService.creaBundle(
                testDistributore.getId(),
                "Cesto Natalizio",
                new BigDecimal("50.00")
        );

        // ASSERT
        assertThat(dto).isNotNull();
        assertThat(dto.nome()).isEqualTo("Cesto Natalizio");
        assertThat(dto.prezzo()).isEqualByComparingTo("50.00");
        assertThat(dto.items()).isEmpty(); // Appena creato è vuoto

        // Verifica sul DB
        assertThat(bundleRepo.count()).isEqualTo(1);
    }

    @Test
    void testCreaBundle_Fail_NomeDuplicato() {
        // ARRANGE (Creo un primo bundle)
        distributoreService.creaBundle(testDistributore.getId(), "Cesto Natalizio", new BigDecimal("50.00"));

        // ACT & ASSERT (Provo a crearne un altro con lo stesso nome)
        assertThatThrownBy(() -> {
            distributoreService.creaBundle(testDistributore.getId(), "cesto natalizio", new BigDecimal("60.00"));
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Bundle già esistente");
    }

    // --- Test aggiungiProdottoABundle ---

    @Test
    void testAggiungiProdottoABundle_Successo_NuovoItem() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));

        // ACT
        BundleDTO dtoAggiornato = distributoreService.aggiungiProdottoABundle(
                bundleDto.id(),
                prodottoApprovato1.getId(),
                3 // Quantità
        );

        // ASSERT
        assertThat(dtoAggiornato.items()).hasSize(1);
        assertThat(dtoAggiornato.items().get(0).prodottoId()).isEqualTo(prodottoApprovato1.getId());
        assertThat(dtoAggiornato.items().get(0).quantita()).isEqualTo(3);

        // Verifica DB
        assertThat(bundleItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAggiungiProdottoABundle_Successo_AggiornaQuantita() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));
        // Aggiungo una prima volta
        distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 3);

        // ACT (Aggiungo LO STESSO prodotto)
        BundleDTO dtoAggiornato = distributoreService.aggiungiProdottoABundle(
                bundleDto.id(),
                prodottoApprovato1.getId(),
                2 // Quantità aggiuntiva
        );

        // ASSERT
        assertThat(dtoAggiornato.items()).hasSize(1); // Sempre 1 item
        assertThat(dtoAggiornato.items().get(0).quantita()).isEqualTo(5); // 3 + 2 = 5

        // Verifica DB
        assertThat(bundleItemRepo.count()).isEqualTo(1);
    }

    @Test
    void testAggiungiProdottoABundle_Fail_ProdottoNonApprovato() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));

        // ACT & ASSERT
        assertThatThrownBy(() -> {
            distributoreService.aggiungiProdottoABundle(
                    bundleDto.id(),
                    prodottoInValidazione.getId(), // Prodotto non approvato!
                    1
            );
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo prodotti APPROVATI");
    }

    @Test
    void testAggiungiProdottoABundle_Fail_QuantitaInvalida() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));

        // ACT & ASSERT (Quantità 0)
        assertThatThrownBy(() -> {
            distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 0);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Quantità deve essere > 0");
    }

    // --- Test aggiornaQuantitaInBundle ---

    @Test
    void testAggiornaQuantita_Successo_Modifica() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));
        bundleDto = distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 5);

        // ACT
        BundleDTO dtoAggiornato = distributoreService.aggiornaQuantitaInBundle(
                bundleDto.id(),
                prodottoApprovato1.getId(),
                2 // Nuova quantità
        );

        // ASSERT
        assertThat(dtoAggiornato.items()).hasSize(1);
        assertThat(dtoAggiornato.items().get(0).quantita()).isEqualTo(2);
    }

    @Test
    void testAggiornaQuantita_Successo_RimuoviConZero() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));
        bundleDto = distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 5);
        bundleDto = distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato2.getId(), 3);

        assertThat(bundleDto.items()).hasSize(2); // Pre-condizione

        // ACT (Imposto quantità a 0 per rimuovere)
        BundleDTO dtoAggiornato = distributoreService.aggiornaQuantitaInBundle(
                bundleDto.id(),
                prodottoApprovato1.getId(),
                0
        );

        // ASSERT
        assertThat(dtoAggiornato.items()).hasSize(1); // Ora solo 1 item
        assertThat(dtoAggiornato.items().get(0).prodottoId()).isEqualTo(prodottoApprovato2.getId());

        // Verifica DB
        assertThat(bundleItemRepo.count()).isEqualTo(1);
        assertThat(bundleRepo.findGraphById(bundleDto.id()).get().getItems()).hasSize(1);
    }

    @Test
    void testAggiornaQuantita_Fail_ProdottoNonNelBundle() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));
        // Aggiungo prodotto 1
        distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 5);

        // ACT & ASSERT (Provo ad aggiornare prodotto 2, che non c'è)
        assertThatThrownBy(() -> {
            distributoreService.aggiornaQuantitaInBundle(bundleDto.id(), prodottoApprovato2.getId(), 2);
        })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Prodotto non presente nel bundle");
    }

    // --- Test rimuoviProdottoDaBundle ---

    @Test
    void testRimuoviProdottoDaBundle_Successo() {
        // ARRANGE
        BundleDTO bundleDto = distributoreService.creaBundle(testDistributore.getId(), "Bundle 1", new BigDecimal("100.00"));
        bundleDto = distributoreService.aggiungiProdottoABundle(bundleDto.id(), prodottoApprovato1.getId(), 5);
        assertThat(bundleDto.items()).hasSize(1); // Pre-condizione

        // ACT
        BundleDTO dtoAggiornato = distributoreService.rimuoviProdottoDaBundle(
                bundleDto.id(),
                prodottoApprovato1.getId()
        );

        // ASSERT
        assertThat(dtoAggiornato.items()).isEmpty(); // Bundle vuoto
        assertThat(bundleItemRepo.count()).isZero(); // Item rimosso
    }
}