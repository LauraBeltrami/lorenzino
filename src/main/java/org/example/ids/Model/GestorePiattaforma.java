package org.example.ids.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "gestori_piattaforma")
public class GestorePiattaforma extends AbstractUtente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String stato; // es: "ATTIVO", "SOSPESO"

    public GestorePiattaforma(Long id, String nome, String email, String password) {
        super();
        this.id = id; this.nome = nome;
        this.email = email;
        this.password = password;
        this.ruolo = "ROLE_VENDITORE";
        this.approvato = false; // Serve approvazione
    }

    // Costruttore vuoto richiesto da JPA
    public GestorePiattaforma() {}

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    // azioni sul profilo utente
    public void assegnaRuolo(UtenteGenerico utente, Ruolo ruolo) {
        if (utente != null && ruolo != null) {
            utente.aggiungiRuolo(ruolo);
        }
    }

    public void rimuoviRuolo(UtenteGenerico utente, Ruolo ruolo) {
        if (utente != null && ruolo != null) {
            utente.rimuoviRuolo(ruolo);
        }
    }
}

