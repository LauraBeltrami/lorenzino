package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "eventi", indexes = @Index(name="idx_evento_animatore", columnList = "animatore_id"))
public class Evento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false) private String titolo;
    private String descrizione;
    @NotBlank @Column(nullable = false) private String luogo;

    @NotNull
    @Column(nullable = false) private LocalDateTime inizio;
    @NotNull @Column(nullable = false) private LocalDateTime fine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animatore_id", nullable = false)
    private Animatore animatore;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvitoEvento> inviti = new HashSet<>();

    public Evento() {}
    public Evento(Long id, String titolo, String descrizione, String luogo,
                  LocalDateTime inizio, LocalDateTime fine, Animatore animatore) {
        this.id = id; this.titolo = titolo; this.descrizione = descrizione; this.luogo = luogo;
        this.inizio = inizio; this.fine = fine; this.animatore = animatore;
    }

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getLuogo() {
        return luogo;
    }

    public LocalDateTime getInizio() {
        return inizio;
    }

    public LocalDateTime getFine() {
        return fine;
    }

    public Animatore getAnimatore() {
        return animatore;
    }

    public Set<InvitoEvento> getInviti() {
        return inviti;
    }
}

