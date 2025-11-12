package Model;

import jakarta.persistence.*;

@Entity
@Table(name = "inviti_evento",
        indexes = { @Index(name="idx_invito_evento", columnList = "evento_id"),
                @Index(name="idx_invito_venditore", columnList = "venditore_id") },
        uniqueConstraints = @UniqueConstraint(name="uk_evento_venditore", columnNames = {"evento_id","venditore_id"}))
public class InvitoEvento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // Distributore è un Venditore → ok usare sempre Venditore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venditore_id", nullable = false)
    private Venditore venditore;

    // solo una nota libera (facoltativa)
    private String nota;

    public InvitoEvento() {}
    public InvitoEvento(Evento evento, Venditore venditore, String nota) {
        this.evento = evento; this.venditore = venditore; this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public Venditore getVenditore() {
        return venditore;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getNota() {
        return nota;
    }
}
