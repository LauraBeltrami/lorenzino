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

    public Distributore() { }
    public Distributore(Long id, String nome) { super(nome); }

    public Set<Bundle> getBundles() { return bundles; }
    public void setBundles(Set<Bundle> bundles) { this.bundles = bundles; }
}


