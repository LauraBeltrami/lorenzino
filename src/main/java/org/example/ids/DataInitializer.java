package org.example.ids;

import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final AnimatoreRepository animatoreRepo;
    private final DistributoreRepository distributoreRepo;
    private final VenditoreRepository venditoreRepo;
    private final AcquirenteRepository acquirenteRepo;
    private final CuratoreRepository curatoreRepo;
    private final EventoRepository eventoRepo;
    private final ProdottoRepository prodottoRepo;
    private final BundleRepository bundleRepo;
    private final BundleItemRepository bundleItemRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AnimatoreRepository animatoreRepo, DistributoreRepository distributoreRepo,
                           VenditoreRepository venditoreRepo,
                           AcquirenteRepository acquirenteRepo,
                           CuratoreRepository curatoreRepo,
                           EventoRepository eventoRepo,
                           ProdottoRepository prodottoRepo,
                           BundleRepository bundleRepo,
                           BundleItemRepository bundleItemRepo,
                           PasswordEncoder passwordEncoder) {
        this.animatoreRepo = animatoreRepo;
        this.distributoreRepo = distributoreRepo;
        this.venditoreRepo = venditoreRepo;
        this.acquirenteRepo = acquirenteRepo;
        this.curatoreRepo = curatoreRepo;
        this.eventoRepo = eventoRepo;
        this.prodottoRepo = prodottoRepo;
        this.bundleRepo = bundleRepo;
        this.bundleItemRepo = bundleItemRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 INIZIO POPOLAMENTO DATI...");

        // Nota: Password per tutti (tranne acquirente) è: "pass"

        // --- 1. ANIMATORI ---
        // Animatore Approvato
        Animatore a1 = new Animatore(null, "DJ Francesco", "dj@mail.com", passwordEncoder.encode("pass"));
        a1.setApprovato(true);
        a1 = animatoreRepo.save(a1);

        // Animatore NON Approvato
        Animatore a2 = new Animatore(null, "Animatore Sospeso", "sospeso@mail.com", passwordEncoder.encode("pass"));
        a2.setApprovato(false);
        a2 = animatoreRepo.save(a2);

        // --- 2. VENDITORI E DISTRIBUTORI ---
        // Venditore Approvato
        Venditore v1 = new Venditore(null, "Salumi Rossi", "salumi@mail.com", passwordEncoder.encode("pass"));
        v1.setApprovato(true);
        v1 = venditoreRepo.save(v1);

        // Distributore Approvato
        Distributore d1 = new Distributore(
                null,
                "Vini Langhe Distribuzione",
                "vini@mail.com",
                passwordEncoder.encode("pass")
        );
        d1.setApprovato(true);
        d1 = venditoreRepo.save(d1);

        // --- 3. CURATORI ---
        // Curatore Approvato
        Curatore c1 = new Curatore(null, "Mario Artista", "curatore@mail.com", passwordEncoder.encode("pass"));
        c1.setApprovato(true);
        c1 = curatoreRepo.save(c1);



        // --- 4. ACQUIRENTI ---
        // Acquirente (password: luca)
        Acquirente acq1 = new Acquirente(null, "Luca", "Bianchi", "luca@mail.com", passwordEncoder.encode("luca"));
        acq1 = acquirenteRepo.save(acq1);

        // --- 5. EVENTI ---
        Evento e1 = new Evento(null, "Fiera del Tartufo", "Una grande festa", "Alba",
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), a1);
        e1 = eventoRepo.save(e1);

        // --- 6. PRODOTTI ---
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

        // --- 7. BUNDLE ---
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
        System.out.println("🔍 RIEPILOGO DATI PER POSTMAN (Login: Basic Auth)");
        System.out.println("==================================================");

        System.out.println("👤 ANIMATORI:");
        System.out.println("   [ID: " + a1.getId() + "] User: dj@mail.com       | Pass: pass  (Approvato: " + a1.isApprovato() + ")");
        System.out.println("   [ID: " + a2.getId() + "] User: sospeso@mail.com  | Pass: pass  (Approvato: " + a2.isApprovato() + ")");

        System.out.println("\n🏪 VENDITORI / DISTRIBUTORI:");
        System.out.println("   [ID: " + v1.getId() + "] User: salumi@mail.com   | Pass: pass  (Venditore)");
        System.out.println("   [ID: " + d1.getId() + "] User: vini@mail.com     | Pass: pass  (Distributore)");

        System.out.println("\n🎨 CURATORI:");
        System.out.println("   [ID: " + c1.getId() + "] User: curatore@mail.com | Pass: pass");

        System.out.println("\n🛒 ACQUIRENTI:");
        System.out.println("   [ID: " + acq1.getId() + "] User: luca@mail.com     | Pass: luca");

        System.out.println("\n--------------------------------------------------");
        System.out.println("📦 OGGETTI DI PROVA:");
        System.out.println("   Evento ID: " + e1.getId() + " (" + e1.getTitolo() + ")");
        System.out.println("   Prodotto ID: " + p1.getId() + " (Salame) | Stock: " + p1.getQuantitaDisponibile());
        System.out.println("   Bundle ID: " + b1.getId() + " (Cesto) | Stock: " + b1.getQuantitaDisponibile());
        System.out.println("==================================================\n");
    }
}