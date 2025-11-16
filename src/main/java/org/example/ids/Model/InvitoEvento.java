package org.example.ids.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoInvito stato = StatoInvito.IN_ATTESA;
    // solo una nota libera (facoltativa)
    private String nota;


    public InvitoEvento(Evento evento, Venditore venditore, StatoInvito stato, String nota) {
        this.evento = evento;
        this.venditore = venditore;
        this.stato = stato;
        this.nota = nota;
    }
}
