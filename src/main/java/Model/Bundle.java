package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bundles",
        indexes = @Index(name = "idx_bundle_distributore", columnList = "distributore_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_bundle_nome_per_distributore",
                columnNames = {"distributore_id","nome"}))
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prezzo; // deciso dal distributore

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "distributore_id", nullable = false)
    private Distributore distributore;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BundleItem> items = new HashSet<>();

    public Bundle() { }
    public Bundle(Long id, String nome, BigDecimal prezzo, Distributore distributore) {
        this.id = id; this.nome = nome; this.prezzo = prezzo; this.distributore = distributore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
    public Distributore getDistributore() { return distributore; }
    public void setDistributore(Distributore distributore) { this.distributore = distributore; }
    public Set<BundleItem> getItems() { return items; }
    public void setItems(Set<BundleItem> items) { this.items = items; }
}

