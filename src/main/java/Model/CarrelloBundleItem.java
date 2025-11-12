package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Entity
@Table(name = "carrello_bundle_items",
        indexes = {
                @Index(name = "idx_cbundle_carrello", columnList = "carrello_id"),
                @Index(name = "idx_cbundle_bundle", columnList = "bundle_id")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_carrello_bundle",
                columnNames = {"carrello_id", "bundle_id"}))
public class CarrelloBundleItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrello_id", nullable = false)
    private Carrello carrello;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bundle_id", nullable = false)
    private Bundle bundle;

    @Min(1) @Column(nullable = false)
    private int quantita;

    protected CarrelloBundleItem() { }

    public CarrelloBundleItem(Long id, Carrello carrello, Bundle bundle, int quantita) {
        this.id = id; this.carrello = carrello; this.bundle = bundle; this.quantita = quantita;
    }
    public CarrelloBundleItem(Carrello carrello, Bundle bundle, int quantita) {
        this(null, carrello, bundle, quantita);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }

    public Bundle getBundle() { return bundle; }
    public void setBundle(Bundle bundle) { this.bundle = bundle; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    @Transient
    public BigDecimal getPrezzoUnitario() {
        return (bundle != null && bundle.getPrezzo() != null) ? bundle.getPrezzo() : BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getTotaleRiga() {
        return getPrezzoUnitario().multiply(BigDecimal.valueOf(quantita));
    }
}

