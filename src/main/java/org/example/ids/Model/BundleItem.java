package org.example.ids.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "bundle_items",
        indexes = {
                @Index(name = "idx_bitem_bundle", columnList = "bundle_id"),
                @Index(name = "idx_bitem_prodotto", columnList = "prodotto_id")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_bundle_prodotto",
                columnNames = {"bundle_id","prodotto_id"}))
public class BundleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bundle_id", nullable = false)
    private Bundle bundle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    @Min(1) @Column(nullable = false)
    private int quantita;

    protected BundleItem() { }
    public BundleItem(Long id, Bundle bundle, Prodotto prodotto, int quantita) {
        this.id = id; this.bundle = bundle; this.prodotto = prodotto; this.quantita = quantita;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bundle getBundle() { return bundle; }
    public void setBundle(Bundle bundle) { this.bundle = bundle; }
    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
}

