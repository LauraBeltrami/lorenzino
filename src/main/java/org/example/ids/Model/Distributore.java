package org.example.ids.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("DISTRIBUTORE")
public class Distributore extends Venditore {

    @OneToMany(mappedBy = "distributore", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bundle> bundles = new HashSet<>();

    // 1. Costruttore vuoto per JPA
    public Distributore() {
        super();
    }

    // 2. Costruttore con parametri per il DataInitializer
    public Distributore(Long id, String nome, String email, String password) {
        // Chiama il costruttore di Venditore
        super(id, nome, email, password);

        // Opzionale: Se vuoi che abbia un ruolo diverso da VENDITORE
        // this.ruolo = "ROLE_DISTRIBUTORE";
    }

    public Set<Bundle> getBundles() { return bundles; }
    public void setBundles(Set<Bundle> bundles) { this.bundles = bundles; }
}