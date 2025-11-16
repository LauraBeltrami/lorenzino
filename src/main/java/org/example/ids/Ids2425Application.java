package org.example.ids;
import org.example.ids.DTO.ProdottoDTO;
import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.example.ids.Service.CarrelloService;
import org.example.ids.Service.DistributoreService;
import org.example.ids.Service.ProdottoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class Ids2425Application {

    public static void main(String[] args) {
        SpringApplication.run(Ids2425Application.class, args);
    }
/*

    @Bean
    CommandLineRunner seedData(
            VenditoreRepository venditoreRepo,
            DistributoreRepository distributoreRepo,
            CuratoreRepository curatoreRepo,
            AcquirenteRepository acquirenteRepo,
            ProdottoService prodottoService,
            DistributoreService distributoreService,
            CarrelloService carrelloService,
            ProdottoRepository prodottoRepo
    ) {
        return args -> {
            // --- Utenti finti ---
            Venditore v1 = venditoreRepo.save(new Venditore(null, "Salumi Rossi"));
            Distributore dist = distributoreRepo.save(new Distributore(null, "Consorzio Tipicità Langhe"));
            Curatore cur = curatoreRepo.save(new Curatore(null, "Curatore Mario"));
            Acquirente a1 = acquirenteRepo.save(new Acquirente(null, "Luca", "Rossi", "luca.rossi@example.com"));

            Prodotto prodotto1 = new Prodotto();
            prodotto1.setNome("pomodoro");
            prodotto1.setPrezzo(new BigDecimal("1.00"));
            prodotto1.setVenditore(v1);

            Prodotto prodotto2 = new Prodotto();
            prodotto2.setNome("cipolla");
            prodotto2.setPrezzo(new BigDecimal("1.00"));
            prodotto2.setVenditore(v1);


            prodottoRepo.save(prodotto1);
            prodottoRepo.save(prodotto2);


            prodottoService.approvaProdotto(prodotto1.getId(),cur.getId(),"babui");


            // --- Prodotti del venditore ---
            ProdottoDTO p1 = prodottoService.creaProdotto(v1.getId(), "Salame Nobile", new BigDecimal("9.90"));
            ProdottoDTO p2 = prodottoService.creaProdotto(v1.getId(), "Formaggio Toma", new BigDecimal("6.50"));
            ProdottoDTO p3 = prodottoService.creaProdotto(v1.getId(), "Grissini Artigianali", new BigDecimal("3.20"));



        };
    }

 */
}
