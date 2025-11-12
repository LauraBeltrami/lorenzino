package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificazioni",
        uniqueConstraints = @UniqueConstraint(name = "uk_cert_prodotto", columnNames = "prodotto_id"))
public class Certificazione {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String descrizione;

    // 1:1 con Prodotto, esiste solo se il prodotto è approvato
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", nullable = false, unique = true)
    private Prodotto prodotto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curatore_validatore_id", nullable = false)
    private Curatore curatoreValidatore;

    @Column(nullable = false)
    private LocalDateTime dataApprovazione;

    public Certificazione() {}
    public Certificazione(Long id, String descrizione, Prodotto prodotto,
                          Curatore curatoreValidatore, LocalDateTime dataApprovazione) {
        this.id = id; this.descrizione = descrizione; this.prodotto = prodotto;
        this.curatoreValidatore = curatoreValidatore; this.dataApprovazione = dataApprovazione;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public Curatore getCuratoreValidatore() { return curatoreValidatore; }
    public void setCuratoreValidatore(Curatore curatoreValidatore) { this.curatoreValidatore = curatoreValidatore; }
    public LocalDateTime getDataApprovazione() { return dataApprovazione; }
    public void setDataApprovazione(LocalDateTime dataApprovazione) { this.dataApprovazione = dataApprovazione; }
}

