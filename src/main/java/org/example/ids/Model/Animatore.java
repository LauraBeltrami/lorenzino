package org.example.ids.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "animatori", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
@Getter
@Setter
public class Animatore extends AbstractUtente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "animatore", fetch = FetchType.LAZY)
    private Set<Evento> eventi = new HashSet<>();

    public Animatore() {}
    public Animatore(Long id, String nome, String email, String password) {
        super();
        this.id = id; this.nome = nome;
        this.email = email;
        this.password = password;
        this.ruolo = "ROLE_ANIMATORE";
        this.approvato = false; // Serve approvazione
    }

// getter/setter...
}

