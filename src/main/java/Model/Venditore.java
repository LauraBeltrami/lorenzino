package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "venditori", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_venditore", length = 20)
public class Venditore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "venditore", fetch = FetchType.LAZY)
    private Set<Prodotto> prodotti = new HashSet<>();

    public Venditore() { }
    public Venditore(Long id, String nome) { this.id = id; this.nome = nome; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Set<Prodotto> getProdotti() { return prodotti; }
    public void setProdotti(Set<Prodotto> prodotti) { this.prodotti = prodotti; }
}
