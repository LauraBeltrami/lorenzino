package Model;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "utenti_generici")
public class UtenteGenerico {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String nome;
    private String email;


    // Salviamo l'enum come stringa in una tabella separata
    @ElementCollection(targetClass = Ruolo.class)
    @CollectionTable(name = "utente_ruoli", joinColumns = @JoinColumn(name = "utente_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo")
    private List<Ruolo> ruoli = new ArrayList<>();


    public UtenteGenerico(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ruoli = new ArrayList<>();
    }


    public UtenteGenerico() {} // richiesto da JPA


    // --- getter/setter ---
    public int getId() { return id; }
    public void setId(Integer id) { this.id = id; }


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public List<Ruolo> getRuoli() { return ruoli; }
    public void setRuoli(List<Ruolo> ruoli) {
        this.ruoli = (ruoli != null) ? ruoli : new ArrayList<>();
    }


    // --- metodi ruolo ---
    public void aggiungiRuolo(Ruolo ruolo) {
        if (ruolo != null && !ruoli.contains(ruolo)) {
            ruoli.add(ruolo);
        }
    }


    public void rimuoviRuolo(Ruolo ruolo) {
        if (ruolo != null) {
            ruoli.remove(ruolo);
        }
    }


    public boolean haRuolo(Ruolo ruolo) {
        return ruolo != null && ruoli.contains(ruolo);
    }


    public boolean haRuolo(String nomeRuolo) {
        Ruolo r = Ruolo.parse(nomeRuolo);
        return r != null && ruoli.contains(r);
    }
}


