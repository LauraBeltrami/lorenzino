package Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "animatori", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
public class Animatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "animatore", fetch = FetchType.LAZY)
    private Set<Evento> eventi = new HashSet<>();

    public Animatore() {}
    public Animatore(Long id, String nome) { this.id = id; this.nome = nome; }

    public String getNome() {
        return nome;
    }

    public Set<Evento> getEventi() {
        return eventi;
    }

    public Long getId() {
        return id;
    }
// getter/setter...
}

