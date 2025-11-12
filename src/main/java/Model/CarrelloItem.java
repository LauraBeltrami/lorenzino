package Model;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carrello_items")
public class CarrelloItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrello_id")
    private Carrello carrello;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodotto_id")
    private Prodotto prodotto;

    @Column(nullable = false)
    private int quantita;

    protected CarrelloItem() { }

    public CarrelloItem(Long id, Carrello carrello, Prodotto prodotto, int quantita) {
        this.id = id; this.carrello = carrello; this.prodotto = prodotto; this.quantita = quantita;
    }
    public CarrelloItem(Carrello carrello, Prodotto prodotto, int quantita) {
        this(null, carrello, prodotto, quantita);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Carrello getCarrello() { return carrello; }
    public void setCarrello(Carrello carrello) { this.carrello = carrello; }
    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    @Transient
    public BigDecimal getPrezzoUnitario() {
        return (prodotto != null && prodotto.getPrezzo() != null) ? prodotto.getPrezzo() : BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getTotaleRiga() {
        return getPrezzoUnitario().multiply(BigDecimal.valueOf(quantita));
    }
}

