package org.example.ids;

import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final AnimatoreRepository animatoreRepo;
    private final VenditoreRepository venditoreRepo;
    private final AcquirenteRepository acquirenteRepo;
    private final CuratoreRepository curatoreRepo;
    private final EventoRepository eventoRepo;
    private final ProdottoRepository prodottoRepo;
    private final BundleRepository bundleRepo;
    private final BundleItemRepository bundleItemRepo;

    public DataInitializer(AnimatoreRepository animatoreRepo,
                           VenditoreRepository venditoreRepo,
                           AcquirenteRepository acquirenteRepo,
                           CuratoreRepository curatoreRepo,
                           EventoRepository eventoRepo,
                           ProdottoRepository prodottoRepo,
                           BundleRepository bundleRepo,
                           BundleItemRepository bundleItemRepo) {
        this.animatoreRepo = animatoreRepo;
        this.venditoreRepo = venditoreRepo;
        this.acquirenteRepo = acquirenteRepo;
        this.curatoreRepo = curatoreRepo;
        this.eventoRepo = eventoRepo;
        this.prodottoRepo = prodottoRepo;
        this.bundleRepo = bundleRepo;
        this.bundleItemRepo = bundleItemRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 INIZIO POPOLAMENTO DATI...");

        // --- 1. UTENTI (ANIMATORI, VENDITORI, ECC) ---

        // Animatore Approvato
        Animatore a1 = new Animatore( "DJ Francesco");
        a1.setApprovato(true);
        a1 = animatoreRepo.save(a1);

        // Animatore NON Approvato
        Animatore a2 = new Animatore("Animatore Sospeso");
        a2.setApprovato(false);
        a2 = animatoreRepo.save(a2);

        // Venditore Approvato
        Venditore v1 = new Venditore( "Salumi Rossi");
        v1.setApprovato(true);
        v1 = venditoreRepo.save(v1);

        // Distributore Approvato
        Distributore d1 = new Distributore( "Vini Langhe Distribuzione");
        d1.setApprovato(true);
        d1 = venditoreRepo.save(d1);

        // Curatore Approvato
        Curatore c1 = new Curatore( "Mario Artista");
        c1.setApprovato(true);
        c1 = curatoreRepo.save(c1);

        // Acquirente
        Acquirente acq1 = new Acquirente(null, "Luca", "Bianchi", "luca@mail.com");
        acq1 = acquirenteRepo.save(acq1);

        // --- 2. EVENTI ---
        Evento e1 = new Evento(null, "Fiera del Tartufo", "Una grande festa", "Alba",
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), a1);
        e1 = eventoRepo.save(e1);

        // --- 3. PRODOTTI ---

        // Prodotto Venditore 1
        Prodotto p1 = new Prodotto(null, "Salame Cacciatorino", new BigDecimal("12.50"), 100);
        p1.setVenditore(v1);
        p1.setStato(StatoProdotto.APPROVATO);
        p1 = prodottoRepo.save(p1);

        // Prodotto Distributore (per bundle)
        Prodotto p2 = new Prodotto(null, "Barolo DOCG 2018", new BigDecimal("45.00"), 50);
        p2.setVenditore(d1);
        p2.setStato(StatoProdotto.APPROVATO);
        p2 = prodottoRepo.save(p2);

        // Altro Prodotto Distributore (per bundle)
        Prodotto p3 = new Prodotto(null, "Grissini Artigianali", new BigDecimal("3.50"), 200);
        p3.setVenditore(d1);
        p3.setStato(StatoProdotto.APPROVATO);
        p3 = prodottoRepo.save(p3);

        // --- 4. BUNDLE ---
        Bundle b1 = new Bundle(null, "Cesto Aperitivo Piemonte", new BigDecimal("40.00"), d1, 10);
        b1 = bundleRepo.save(b1);

        BundleItem item1 = new BundleItem(null, b1, p2, 1); // 1 Barolo
        bundleItemRepo.save(item1);
        BundleItem item2 = new BundleItem(null, b1, p3, 2); // 2 Grissini
        bundleItemRepo.save(item2);

        // ================================================================
        //                     STAMPE PER IL TUO TEST
        // ================================================================
        System.out.println("\n==================================================");
        System.out.println("🔍 RIEPILOGO ID PER POSTMAN (Copia questi ID)");
        System.out.println("==================================================");

        System.out.println("👤 ANIMATORI:");
        System.out.println("   ID: " + a1.getId() + " | Nome: " + a1.getNome() + " (Approvato: " + a1.isApprovato() + ")");
        System.out.println("   ID: " + a2.getId() + " | Nome: " + a2.getNome() + " (Approvato: " + a2.isApprovato() + ")");

        System.out.println("\n🏪 VENDITORI / DISTRIBUTORI:");
        System.out.println("   ID: " + v1.getId() + " | Nome: " + v1.getNome() + " (Venditore Semplice)");
        System.out.println("   ID: " + d1.getId() + " | Nome: " + d1.getNome() + " (Distributore)");

        System.out.println("\n🎨 CURATORI:");
        System.out.println("   ID: " + c1.getId() + " | Nome: " + c1.getNome());

        System.out.println("\n🛒 ACQUIRENTI:");
        System.out.println("   ID: " + acq1.getId() + " | Nome: " + acq1.getNome() + " " + acq1.getCognome() + " | Email: " + acq1.getEmail());

        System.out.println("\n📅 EVENTI:");
        System.out.println("   ID: " + e1.getId() + " | Titolo: " + e1.getTitolo() + " | Creato da Animatore ID: " + e1.getAnimatore().getId());

        System.out.println("\n📦 PRODOTTI SINGOLI:");
        System.out.println("   ID: " + p1.getId() + " | Nome: " + p1.getNome() + " | Prezzo: " + p1.getPrezzo() + " | Stock: " + p1.getQuantitaDisponibile());
        System.out.println("   ID: " + p2.getId() + " | Nome: " + p2.getNome() + " | Prezzo: " + p2.getPrezzo() + " | Stock: " + p2.getQuantitaDisponibile());
        System.out.println("   ID: " + p3.getId() + " | Nome: " + p3.getNome() + " | Prezzo: " + p3.getPrezzo() + " | Stock: " + p3.getQuantitaDisponibile());

        System.out.println("\n🎁 BUNDLE:");
        System.out.println("   ID: " + b1.getId() + " | Nome: " + b1.getNome() + " | Prezzo: " + b1.getPrezzo() + " | Stock Bundle: " + b1.getQuantitaDisponibile());

        System.out.println("==================================================\n");
    }
}