package org.example.ids.Model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "carrelli")
public class Carrello {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1 acquirente → al massimo 1 carrello
    @OneToOne
    @JoinColumn(name = "acquirente_id", unique = true)
    private Acquirente acquirente;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarrelloItem> items = new HashSet<>(); // <-- SOLUZIONE

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarrelloBundleItem> bundleItems = new HashSet<>(); // <-- SOLUZIONE

    public Carrello() {}
    public Carrello(Long id, Acquirente acquirente) {
        this.id = id; this.acquirente = acquirente;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Acquirente getAcquirente() { return acquirente; }
    public void setAcquirente(Acquirente acquirente) { this.acquirente = acquirente; }

    public Set<CarrelloItem> getItems() { return items; }
    public void setItems(Set<CarrelloItem> items) { this.items = items; }

    public Set<CarrelloBundleItem> getBundleItems() { return bundleItems; }
    public void setBundleItems(Set<CarrelloBundleItem> bundleItems) { this.bundleItems = bundleItems; }

    /** Numero complessivo di unità: somma quantità prodotti + quantità bundle (ogni bundle conta come 1 unità per quantità). */
    public int getNumeroArticoli() {
        int prodotti = items.stream().mapToInt(CarrelloItem::getQuantita).sum();
        int bundles = bundleItems.stream().mapToInt(CarrelloBundleItem::getQuantita).sum();
        return prodotti + bundles;
    }

    /** Totale = somma righe prodotti + somma righe bundle. */
    public BigDecimal getTotale() {
        BigDecimal totProdotti = items.stream()
                .map(CarrelloItem::getTotaleRiga)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totBundle = bundleItems.stream()
                .map(CarrelloBundleItem::getTotaleRiga)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totProdotti.add(totBundle);
    }
}